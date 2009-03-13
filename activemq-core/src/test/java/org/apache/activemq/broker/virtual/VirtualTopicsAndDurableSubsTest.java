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
name|broker
operator|.
name|virtual
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
name|MessageConsumer
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|MessageProducer
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
name|apache
operator|.
name|activemq
operator|.
name|broker
operator|.
name|jmx
operator|.
name|MBeanTest
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

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|spring
operator|.
name|ConsumerBean
import|;
end_import

begin_class
specifier|public
class|class
name|VirtualTopicsAndDurableSubsTest
extends|extends
name|MBeanTest
block|{
specifier|private
name|Connection
name|connection
decl_stmt|;
specifier|public
name|void
name|testVirtualTopicCreationAndDurableSubs
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|connection
operator|==
literal|null
condition|)
block|{
name|connection
operator|=
name|createConnection
argument_list|()
expr_stmt|;
block|}
name|connection
operator|.
name|setClientID
argument_list|(
name|getAClientID
argument_list|()
argument_list|)
expr_stmt|;
name|connection
operator|.
name|start
argument_list|()
expr_stmt|;
name|ConsumerBean
name|messageList
init|=
operator|new
name|ConsumerBean
argument_list|()
decl_stmt|;
name|messageList
operator|.
name|setVerbose
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|String
name|queueAName
init|=
name|getVirtualTopicConsumerName
argument_list|()
decl_stmt|;
comment|// create consumer 'cluster'
name|ActiveMQQueue
name|queue1
init|=
operator|new
name|ActiveMQQueue
argument_list|(
name|queueAName
argument_list|)
decl_stmt|;
name|ActiveMQQueue
name|queue2
init|=
operator|new
name|ActiveMQQueue
argument_list|(
name|queueAName
argument_list|)
decl_stmt|;
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
name|MessageConsumer
name|c1
init|=
name|session
operator|.
name|createConsumer
argument_list|(
name|queue1
argument_list|)
decl_stmt|;
name|MessageConsumer
name|c2
init|=
name|session
operator|.
name|createConsumer
argument_list|(
name|queue2
argument_list|)
decl_stmt|;
name|c1
operator|.
name|setMessageListener
argument_list|(
name|messageList
argument_list|)
expr_stmt|;
name|c2
operator|.
name|setMessageListener
argument_list|(
name|messageList
argument_list|)
expr_stmt|;
comment|// create topic producer
name|MessageProducer
name|producer
init|=
name|session
operator|.
name|createProducer
argument_list|(
operator|new
name|ActiveMQTopic
argument_list|(
name|getVirtualTopicName
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|producer
argument_list|)
expr_stmt|;
name|int
name|total
init|=
literal|10
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|total
condition|;
name|i
operator|++
control|)
block|{
name|producer
operator|.
name|send
argument_list|(
name|session
operator|.
name|createTextMessage
argument_list|(
literal|"message: "
operator|+
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|messageList
operator|.
name|assertMessagesArrived
argument_list|(
name|total
argument_list|)
expr_stmt|;
comment|//Add and remove durable subscriber after using VirtualTopics
name|assertCreateAndDestroyDurableSubscriptions
argument_list|()
expr_stmt|;
block|}
specifier|protected
name|String
name|getAClientID
parameter_list|()
block|{
return|return
literal|"VirtualTopicCreationAndDurableSubs"
return|;
block|}
specifier|protected
name|String
name|getVirtualTopicName
parameter_list|()
block|{
return|return
literal|"VirtualTopic.TEST"
return|;
block|}
specifier|protected
name|String
name|getVirtualTopicConsumerName
parameter_list|()
block|{
return|return
literal|"Consumer.A.VirtualTopic.TEST"
return|;
block|}
specifier|protected
name|String
name|getDurableSubscriberName
parameter_list|()
block|{
return|return
literal|"Sub1"
return|;
block|}
specifier|protected
name|String
name|getDurableSubscriberTopicName
parameter_list|()
block|{
return|return
literal|"simple.topic"
return|;
block|}
specifier|protected
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|connection
operator|!=
literal|null
condition|)
block|{
name|connection
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
block|}
comment|//Overrides test cases from MBeanTest to avoid having them run.
specifier|public
name|void
name|testMBeans
parameter_list|()
throws|throws
name|Exception
block|{}
specifier|public
name|void
name|testMoveMessages
parameter_list|()
throws|throws
name|Exception
block|{}
specifier|public
name|void
name|testRetryMessages
parameter_list|()
throws|throws
name|Exception
block|{}
specifier|public
name|void
name|testMoveMessagesBySelector
parameter_list|()
throws|throws
name|Exception
block|{}
specifier|public
name|void
name|testCopyMessagesBySelector
parameter_list|()
throws|throws
name|Exception
block|{}
block|}
end_class

end_unit

