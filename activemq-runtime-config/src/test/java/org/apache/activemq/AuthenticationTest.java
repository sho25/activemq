begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|activemq
package|;
end_package

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|JMSException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|Session
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertTrue
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|fail
import|;
end_import

begin_class
specifier|public
class|class
name|AuthenticationTest
extends|extends
name|RuntimeConfigTestSupport
block|{
name|String
name|configurationSeed
init|=
literal|"authenticationTest"
decl_stmt|;
annotation|@
name|Test
specifier|public
name|void
name|testMod
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|String
name|brokerConfig
init|=
name|configurationSeed
operator|+
literal|"-authentication-broker"
decl_stmt|;
name|applyNewConfig
argument_list|(
name|brokerConfig
argument_list|,
name|configurationSeed
operator|+
literal|"-users"
argument_list|)
expr_stmt|;
name|startBroker
argument_list|(
name|brokerConfig
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"broker alive"
argument_list|,
name|brokerService
operator|.
name|isStarted
argument_list|()
argument_list|)
expr_stmt|;
name|assertAllowed
argument_list|(
literal|"test_user_password"
argument_list|,
literal|"USERS.A"
argument_list|)
expr_stmt|;
name|assertDenied
argument_list|(
literal|"another_test_user_password"
argument_list|,
literal|"USERS.A"
argument_list|)
expr_stmt|;
comment|// anonymous
name|assertDenied
argument_list|(
literal|null
argument_list|,
literal|"USERS.A"
argument_list|)
expr_stmt|;
name|applyNewConfig
argument_list|(
name|brokerConfig
argument_list|,
name|configurationSeed
operator|+
literal|"-two-users"
argument_list|,
name|SLEEP
argument_list|)
expr_stmt|;
name|assertAllowed
argument_list|(
literal|"test_user_password"
argument_list|,
literal|"USERS.A"
argument_list|)
expr_stmt|;
name|assertAllowed
argument_list|(
literal|"another_test_user_password"
argument_list|,
literal|"USERS.A"
argument_list|)
expr_stmt|;
name|assertAllowed
argument_list|(
literal|null
argument_list|,
literal|"USERS.A"
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|assertDenied
parameter_list|(
name|String
name|userPass
parameter_list|,
name|String
name|destination
parameter_list|)
block|{
try|try
block|{
name|assertAllowed
argument_list|(
name|userPass
argument_list|,
name|destination
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Expected not allowed exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|JMSException
name|expected
parameter_list|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"got:"
operator|+
name|expected
argument_list|,
name|expected
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|assertAllowed
parameter_list|(
name|String
name|userPass
parameter_list|,
name|String
name|dest
parameter_list|)
throws|throws
name|JMSException
block|{
name|ActiveMQConnection
name|connection
init|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
literal|"vm://localhost"
argument_list|)
operator|.
name|createActiveMQConnection
argument_list|(
name|userPass
argument_list|,
name|userPass
argument_list|)
decl_stmt|;
name|connection
operator|.
name|start
argument_list|()
expr_stmt|;
try|try
block|{
name|Session
name|session
init|=
name|connection
operator|.
name|createSession
argument_list|(
literal|false
argument_list|,
name|Session
operator|.
name|AUTO_ACKNOWLEDGE
argument_list|)
decl_stmt|;
name|session
operator|.
name|createConsumer
argument_list|(
name|session
operator|.
name|createQueue
argument_list|(
name|dest
argument_list|)
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|connection
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

