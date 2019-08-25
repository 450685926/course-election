package com.server.edu.election;

import org.junit.Test;
import org.springframework.test.context.ActiveProfiles;

import com.server.edu.election.rpc.CultureSerivceInvoker;

@ActiveProfiles("dev")
public class CultureRpcTest extends ApplicationTest
{
    @Test
    public void test() throws Exception
    {
        CultureSerivceInvoker.getCoursesLevel();
    }
    
}
