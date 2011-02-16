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
name|usecases
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
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|ActiveMQConnectionFactory
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

begin_comment
comment|/**  *   */
end_comment

begin_class
specifier|public
class|class
name|TwoBrokerTopicSendReceiveUsingJavaConfigurationTest
extends|extends
name|TwoBrokerTopicSendReceiveTest
block|{
name|BrokerService
name|receiveBroker
decl_stmt|;
name|BrokerService
name|sendBroker
decl_stmt|;
specifier|protected
name|ActiveMQConnectionFactory
name|createReceiverConnectionFactory
parameter_list|()
throws|throws
name|JMSException
block|{
try|try
block|{
name|receiveBroker
operator|=
operator|new
name|BrokerService
argument_list|()
expr_stmt|;
name|receiveBroker
operator|.
name|setBrokerName
argument_list|(
literal|"receiveBroker"
argument_list|)
expr_stmt|;
name|receiveBroker
operator|.
name|setUseJmx
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|receiveBroker
operator|.
name|setPersistent
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|receiveBroker
operator|.
name|addConnector
argument_list|(
literal|"tcp://localhost:62002"
argument_list|)
expr_stmt|;
name|receiveBroker
operator|.
name|addNetworkConnector
argument_list|(
literal|"static:failover:tcp://localhost:62001"
argument_list|)
expr_stmt|;
name|receiveBroker
operator|.
name|start
argument_list|()
expr_stmt|;
name|ActiveMQConnectionFactory
name|factory
init|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
literal|"tcp://localhost:62002"
argument_list|)
decl_stmt|;
return|return
name|factory
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
specifier|protected
name|ActiveMQConnectionFactory
name|createSenderConnectionFactory
parameter_list|()
throws|throws
name|JMSException
block|{
try|try
block|{
name|sendBroker
operator|=
operator|new
name|BrokerService
argument_list|()
expr_stmt|;
name|sendBroker
operator|.
name|setBrokerName
argument_list|(
literal|"sendBroker"
argument_list|)
expr_stmt|;
name|sendBroker
operator|.
name|setUseJmx
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|sendBroker
operator|.
name|setPersistent
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|sendBroker
operator|.
name|addConnector
argument_list|(
literal|"tcp://localhost:62001"
argument_list|)
expr_stmt|;
name|sendBroker
operator|.
name|addNetworkConnector
argument_list|(
literal|"static:failover:tcp://localhost:62002"
argument_list|)
expr_stmt|;
name|sendBroker
operator|.
name|start
argument_list|()
expr_stmt|;
name|ActiveMQConnectionFactory
name|factory
init|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
literal|"tcp://localhost:62001"
argument_list|)
decl_stmt|;
return|return
name|factory
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
specifier|protected
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
if|if
condition|(
name|sendBroker
operator|!=
literal|null
condition|)
block|{
name|sendBroker
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|receiveBroker
operator|!=
literal|null
condition|)
block|{
name|receiveBroker
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

