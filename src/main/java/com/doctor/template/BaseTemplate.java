package com.doctor.template;

import static net.grinder.script.Grinder.grinder;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.util.Random;
import java.util.UUID;

import org.junit.Test;
import org.junit.runner.RunWith;

import HTTPClient.HTTPResponse;
import net.grinder.common.GrinderException;
import net.grinder.plugin.http.HTTPRequest;
import net.grinder.script.GTest;
import net.grinder.script.InvalidContextException;
import net.grinder.scriptengine.groovy.junit.GrinderRunner;
import net.grinder.scriptengine.groovy.junit.annotation.BeforeProcess;
import net.grinder.scriptengine.groovy.junit.annotation.BeforeThread;

/**
 * @description
 *
 * @author sdcuike
 *
 * @date 2016年7月12日 下午4:21:27
 * 
 *       注意：-javaagent:D:\repository\net\sf\grinder\grinder-dcr-agent\3.9.1\
 *       grinder-dcr-agent-3.9.1.jar
 */
@RunWith(GrinderRunner.class)
public class BaseTemplate {

	public static GTest test;
	public static HTTPRequest request;
	Random random = new Random();

	// This method is executed once per a process.
	@BeforeProcess
	public static void beforeClass() throws GrinderException {
		test = new GTest(1, UUID.randomUUID().toString());
		// Register a custom statistics
		grinder.getStatistics().registerSummaryExpression("User_defined", "(/ userLong0(+ (count timedTests)))");
		request = new HTTPRequest();
		test.record(request);

		grinder.getLogger().info("before process.");
	}

	// This method is executed once per a thread.
	@BeforeThread
	public void beforeThread() throws InvalidContextException {
		grinder.getStatistics().setDelayReports(true);
		grinder.getLogger().info("before thread.");
	}

	// This method is continuously executed until you stop the test
	@Test
	public void test() throws Exception {
		HTTPResponse result = request.GET("https://www.baidu.com");
		int deliveryTime = random.nextInt(1000);
		grinder.sleep(deliveryTime);
		grinder.getLogger().info("deliveryTime: " + deliveryTime);
		grinder.getStatistics().getForLastTest().setLong("userLong0", deliveryTime);
		if (result.getStatusCode() == 301 || result.getStatusCode() == 302) {
			grinder.getLogger().warn("Warning. The response may not be correct. The response code was {}.",
					result.getStatusCode());
		} else {
			assertThat(result.getStatusCode(), is(200));
		}
	}
}
