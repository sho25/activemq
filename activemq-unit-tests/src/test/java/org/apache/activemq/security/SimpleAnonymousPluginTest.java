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
operator|.
name|security
package|;
end_package

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|Connection
import|;
end_import

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
name|junit
operator|.
name|framework
operator|.
name|Test
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|CombinationTestSupport
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|broker
operator|.
name|BrokerService
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|command
operator|.
name|ActiveMQQueue
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|command
operator|.
name|ActiveMQTopic
import|;
end_import

begin_class
specifier|public
class|class
name|SimpleAnonymousPluginTest
extends|extends
name|SimpleAuthenticationPluginTest
block|{
specifier|public
specifier|static
name|Test
name|suite
parameter_list|()
block|{
return|return
name|suite
argument_list|(
name|SimpleAnonymousPluginTest
operator|.
name|class
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
block|{
name|junit
operator|.
name|textui
operator|.
name|TestRunner
operator|.
name|run
argument_list|(
name|suite
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|BrokerService
name|createBroker
parameter_list|()
throws|throws
name|Exception
block|{
return|return
name|createBroker
argument_list|(
literal|"org/apache/activemq/security/simple-anonymous-broker.xml"
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|testInvalidAuthentication
parameter_list|()
throws|throws
name|JMSException
block|{
try|try
block|{
comment|// Bad password
name|Connection
name|c
init|=
name|factory
operator|.
name|createConnection
argument_list|(
literal|"user"
argument_list|,
literal|"krap"
argument_list|)
decl_stmt|;
name|connections
operator|.
name|add
argument_list|(
name|c
argument_list|)
expr_stmt|;
name|c
operator|.
name|start
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"Expected exception."
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|JMSException
name|e
parameter_list|)
block|{         }
try|try
block|{
comment|// Bad userid
name|Connection
name|c
init|=
name|factory
operator|.
name|createConnection
argument_list|(
literal|"userkrap"
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|connections
operator|.
name|add
argument_list|(
name|c
argument_list|)
expr_stmt|;
name|c
operator|.
name|start
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"Expected exception."
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|JMSException
name|e
parameter_list|)
block|{         }
block|}
specifier|public
name|void
name|testAnonymousReceiveSucceeds
parameter_list|()
throws|throws
name|JMSException
block|{
name|doReceive
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testAnonymousReceiveFails
parameter_list|()
throws|throws
name|JMSException
block|{
name|doReceive
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testAnonymousSendFails
parameter_list|()
throws|throws
name|JMSException
block|{
name|doSend
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testAnonymousSendSucceeds
parameter_list|()
throws|throws
name|JMSException
block|{
name|doSend
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
comment|/**      * @see {@link CombinationTestSupport}      */
specifier|public
name|void
name|initCombosForTestAnonymousReceiveSucceeds
parameter_list|()
block|{
name|addCombinationValues
argument_list|(
literal|"userName"
argument_list|,
operator|new
name|Object
index|[]
block|{
literal|null
block|}
argument_list|)
expr_stmt|;
name|addCombinationValues
argument_list|(
literal|"password"
argument_list|,
operator|new
name|Object
index|[]
block|{
literal|null
block|}
argument_list|)
expr_stmt|;
name|addCombinationValues
argument_list|(
literal|"destination"
argument_list|,
operator|new
name|Object
index|[]
block|{
operator|new
name|ActiveMQQueue
argument_list|(
literal|"GUEST.BAR"
argument_list|)
block|,
operator|new
name|ActiveMQTopic
argument_list|(
literal|"GUEST.BAR"
argument_list|)
block|}
argument_list|)
expr_stmt|;
block|}
comment|/**      * @see {@link CombinationTestSupport}      */
specifier|public
name|void
name|initCombosForTestAnonymousReceiveFails
parameter_list|()
block|{
name|addCombinationValues
argument_list|(
literal|"userName"
argument_list|,
operator|new
name|Object
index|[]
block|{
literal|null
block|}
argument_list|)
expr_stmt|;
name|addCombinationValues
argument_list|(
literal|"password"
argument_list|,
operator|new
name|Object
index|[]
block|{
literal|null
block|}
argument_list|)
expr_stmt|;
name|addCombinationValues
argument_list|(
literal|"destination"
argument_list|,
operator|new
name|Object
index|[]
block|{
operator|new
name|ActiveMQQueue
argument_list|(
literal|"TEST"
argument_list|)
block|,
operator|new
name|ActiveMQTopic
argument_list|(
literal|"TEST"
argument_list|)
block|,
operator|new
name|ActiveMQQueue
argument_list|(
literal|"USERS.FOO"
argument_list|)
block|,
operator|new
name|ActiveMQTopic
argument_list|(
literal|"USERS.FOO"
argument_list|)
block|}
argument_list|)
expr_stmt|;
block|}
comment|/**      * @see {@link CombinationTestSupport}      */
specifier|public
name|void
name|initCombosForTestAnonymousSendFails
parameter_list|()
block|{
name|addCombinationValues
argument_list|(
literal|"userName"
argument_list|,
operator|new
name|Object
index|[]
block|{
literal|null
block|}
argument_list|)
expr_stmt|;
name|addCombinationValues
argument_list|(
literal|"password"
argument_list|,
operator|new
name|Object
index|[]
block|{
literal|null
block|}
argument_list|)
expr_stmt|;
name|addCombinationValues
argument_list|(
literal|"destination"
argument_list|,
operator|new
name|Object
index|[]
block|{
operator|new
name|ActiveMQQueue
argument_list|(
literal|"TEST"
argument_list|)
block|,
operator|new
name|ActiveMQTopic
argument_list|(
literal|"TEST"
argument_list|)
block|,
operator|new
name|ActiveMQQueue
argument_list|(
literal|"USERS.FOO"
argument_list|)
block|,
operator|new
name|ActiveMQTopic
argument_list|(
literal|"USERS.FOO"
argument_list|)
block|}
argument_list|)
expr_stmt|;
block|}
comment|/**      * @see {@link CombinationTestSupport}      */
specifier|public
name|void
name|initCombosForTestAnonymousSendSucceeds
parameter_list|()
block|{
name|addCombinationValues
argument_list|(
literal|"userName"
argument_list|,
operator|new
name|Object
index|[]
block|{
literal|null
block|}
argument_list|)
expr_stmt|;
name|addCombinationValues
argument_list|(
literal|"password"
argument_list|,
operator|new
name|Object
index|[]
block|{
literal|null
block|}
argument_list|)
expr_stmt|;
name|addCombinationValues
argument_list|(
literal|"destination"
argument_list|,
operator|new
name|Object
index|[]
block|{
operator|new
name|ActiveMQQueue
argument_list|(
literal|"GUEST.BAR"
argument_list|)
block|,
operator|new
name|ActiveMQTopic
argument_list|(
literal|"GUEST.BAR"
argument_list|)
block|}
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

