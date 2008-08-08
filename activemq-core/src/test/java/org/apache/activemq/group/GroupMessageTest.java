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
name|group
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicBoolean
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicInteger
import|;
end_import

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
name|ConnectionFactory
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
name|TestCase
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
name|ActiveMQConnection
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

begin_class
specifier|public
class|class
name|GroupMessageTest
extends|extends
name|TestCase
block|{
specifier|protected
name|BrokerService
name|broker
decl_stmt|;
specifier|protected
name|String
name|bindAddress
init|=
name|ActiveMQConnectionFactory
operator|.
name|DEFAULT_BROKER_BIND_URL
decl_stmt|;
specifier|public
name|void
name|testGroupBroadcast
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|int
name|number
init|=
literal|10
decl_stmt|;
specifier|final
name|AtomicInteger
name|count
init|=
operator|new
name|AtomicInteger
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|Connection
argument_list|>
name|connections
init|=
operator|new
name|ArrayList
argument_list|<
name|Connection
argument_list|>
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|Group
argument_list|>
name|groups
init|=
operator|new
name|ArrayList
argument_list|<
name|Group
argument_list|>
argument_list|()
decl_stmt|;
name|ConnectionFactory
name|factory
init|=
name|createConnectionFactory
argument_list|()
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
name|number
condition|;
name|i
operator|++
control|)
block|{
name|Connection
name|connection
init|=
name|factory
operator|.
name|createConnection
argument_list|()
decl_stmt|;
name|connection
operator|.
name|start
argument_list|()
expr_stmt|;
name|connections
operator|.
name|add
argument_list|(
name|connection
argument_list|)
expr_stmt|;
name|Group
name|group
init|=
operator|new
name|Group
argument_list|(
name|connection
argument_list|,
literal|"group"
operator|+
name|i
argument_list|)
decl_stmt|;
name|group
operator|.
name|setHeartBeatInterval
argument_list|(
literal|20000
argument_list|)
expr_stmt|;
if|if
condition|(
name|i
operator|==
name|number
operator|-
literal|1
condition|)
block|{
name|group
operator|.
name|setMinimumGroupSize
argument_list|(
name|number
argument_list|)
expr_stmt|;
block|}
name|group
operator|.
name|start
argument_list|()
expr_stmt|;
name|groups
operator|.
name|add
argument_list|(
name|group
argument_list|)
expr_stmt|;
name|group
operator|.
name|addGroupMessageListener
argument_list|(
operator|new
name|GroupMessageListener
argument_list|()
block|{
specifier|public
name|void
name|messageDelivered
parameter_list|(
name|Member
name|sender
parameter_list|,
name|String
name|replyId
parameter_list|,
name|Object
name|message
parameter_list|)
block|{
synchronized|synchronized
init|(
name|count
init|)
block|{
if|if
condition|(
name|count
operator|.
name|incrementAndGet
argument_list|()
operator|==
name|number
condition|)
block|{
name|count
operator|.
name|notifyAll
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
argument_list|)
expr_stmt|;
block|}
name|groups
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|broadcastMessage
argument_list|(
literal|"hello"
argument_list|)
expr_stmt|;
synchronized|synchronized
init|(
name|count
init|)
block|{
if|if
condition|(
name|count
operator|.
name|get
argument_list|()
operator|<
name|number
condition|)
block|{
name|count
operator|.
name|wait
argument_list|(
literal|5000
argument_list|)
expr_stmt|;
block|}
block|}
name|assertEquals
argument_list|(
name|number
argument_list|,
name|count
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|Group
name|map
range|:
name|groups
control|)
block|{
name|map
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
for|for
control|(
name|Connection
name|connection
range|:
name|connections
control|)
block|{
name|connection
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|testsendMessage
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|int
name|number
init|=
literal|10
decl_stmt|;
specifier|final
name|AtomicInteger
name|count
init|=
operator|new
name|AtomicInteger
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|Connection
argument_list|>
name|connections
init|=
operator|new
name|ArrayList
argument_list|<
name|Connection
argument_list|>
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|Group
argument_list|>
name|groups
init|=
operator|new
name|ArrayList
argument_list|<
name|Group
argument_list|>
argument_list|()
decl_stmt|;
name|ConnectionFactory
name|factory
init|=
name|createConnectionFactory
argument_list|()
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
name|number
condition|;
name|i
operator|++
control|)
block|{
name|Connection
name|connection
init|=
name|factory
operator|.
name|createConnection
argument_list|()
decl_stmt|;
name|connection
operator|.
name|start
argument_list|()
expr_stmt|;
name|connections
operator|.
name|add
argument_list|(
name|connection
argument_list|)
expr_stmt|;
name|Group
name|group
init|=
operator|new
name|Group
argument_list|(
name|connection
argument_list|,
literal|"group"
operator|+
name|i
argument_list|)
decl_stmt|;
name|group
operator|.
name|setHeartBeatInterval
argument_list|(
literal|20000
argument_list|)
expr_stmt|;
if|if
condition|(
name|i
operator|==
name|number
operator|-
literal|1
condition|)
block|{
name|group
operator|.
name|setMinimumGroupSize
argument_list|(
name|number
argument_list|)
expr_stmt|;
block|}
name|group
operator|.
name|start
argument_list|()
expr_stmt|;
name|groups
operator|.
name|add
argument_list|(
name|group
argument_list|)
expr_stmt|;
name|group
operator|.
name|addGroupMessageListener
argument_list|(
operator|new
name|GroupMessageListener
argument_list|()
block|{
specifier|public
name|void
name|messageDelivered
parameter_list|(
name|Member
name|sender
parameter_list|,
name|String
name|replyId
parameter_list|,
name|Object
name|message
parameter_list|)
block|{
synchronized|synchronized
init|(
name|count
init|)
block|{
name|count
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
name|count
operator|.
name|notifyAll
argument_list|()
expr_stmt|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
block|}
name|groups
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|sendMessage
argument_list|(
literal|"hello"
argument_list|)
expr_stmt|;
synchronized|synchronized
init|(
name|count
init|)
block|{
if|if
condition|(
name|count
operator|.
name|get
argument_list|()
operator|==
literal|0
condition|)
block|{
name|count
operator|.
name|wait
argument_list|(
literal|5000
argument_list|)
expr_stmt|;
block|}
block|}
comment|// wait a while to check that only one got it
name|Thread
operator|.
name|sleep
argument_list|(
literal|2000
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|count
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|Group
name|map
range|:
name|groups
control|)
block|{
name|map
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
for|for
control|(
name|Connection
name|connection
range|:
name|connections
control|)
block|{
name|connection
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|testSendToSingleMember
parameter_list|()
throws|throws
name|Exception
block|{
name|ConnectionFactory
name|factory
init|=
name|createConnectionFactory
argument_list|()
decl_stmt|;
name|Connection
name|connection1
init|=
name|factory
operator|.
name|createConnection
argument_list|()
decl_stmt|;
name|Connection
name|connection2
init|=
name|factory
operator|.
name|createConnection
argument_list|()
decl_stmt|;
name|connection1
operator|.
name|start
argument_list|()
expr_stmt|;
name|connection2
operator|.
name|start
argument_list|()
expr_stmt|;
name|Group
name|group1
init|=
operator|new
name|Group
argument_list|(
name|connection1
argument_list|,
literal|"group1"
argument_list|)
decl_stmt|;
specifier|final
name|AtomicBoolean
name|called
init|=
operator|new
name|AtomicBoolean
argument_list|()
decl_stmt|;
name|group1
operator|.
name|addGroupMessageListener
argument_list|(
operator|new
name|GroupMessageListener
argument_list|()
block|{
specifier|public
name|void
name|messageDelivered
parameter_list|(
name|Member
name|sender
parameter_list|,
name|String
name|replyId
parameter_list|,
name|Object
name|message
parameter_list|)
block|{
synchronized|synchronized
init|(
name|called
init|)
block|{
name|called
operator|.
name|set
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|called
operator|.
name|notifyAll
argument_list|()
expr_stmt|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
name|group1
operator|.
name|start
argument_list|()
expr_stmt|;
name|Group
name|group2
init|=
operator|new
name|Group
argument_list|(
name|connection2
argument_list|,
literal|"group2"
argument_list|)
decl_stmt|;
name|group2
operator|.
name|setMinimumGroupSize
argument_list|(
literal|2
argument_list|)
expr_stmt|;
name|group2
operator|.
name|start
argument_list|()
expr_stmt|;
name|Member
name|member1
init|=
name|group2
operator|.
name|getMemberByName
argument_list|(
literal|"group1"
argument_list|)
decl_stmt|;
name|group2
operator|.
name|sendMessage
argument_list|(
name|member1
argument_list|,
literal|"hello"
argument_list|)
expr_stmt|;
synchronized|synchronized
init|(
name|called
init|)
block|{
if|if
condition|(
operator|!
name|called
operator|.
name|get
argument_list|()
condition|)
block|{
name|called
operator|.
name|wait
argument_list|(
literal|5000
argument_list|)
expr_stmt|;
block|}
block|}
name|assertTrue
argument_list|(
name|called
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|group1
operator|.
name|stop
argument_list|()
expr_stmt|;
name|group2
operator|.
name|stop
argument_list|()
expr_stmt|;
name|connection1
operator|.
name|close
argument_list|()
expr_stmt|;
name|connection2
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|testSendRequestReply
parameter_list|()
throws|throws
name|Exception
block|{
name|ConnectionFactory
name|factory
init|=
name|createConnectionFactory
argument_list|()
decl_stmt|;
name|Connection
name|connection1
init|=
name|factory
operator|.
name|createConnection
argument_list|()
decl_stmt|;
name|Connection
name|connection2
init|=
name|factory
operator|.
name|createConnection
argument_list|()
decl_stmt|;
name|connection1
operator|.
name|start
argument_list|()
expr_stmt|;
name|connection2
operator|.
name|start
argument_list|()
expr_stmt|;
specifier|final
name|int
name|number
init|=
literal|1000
decl_stmt|;
specifier|final
name|AtomicInteger
name|requestCount
init|=
operator|new
name|AtomicInteger
argument_list|()
decl_stmt|;
specifier|final
name|AtomicInteger
name|replyCount
init|=
operator|new
name|AtomicInteger
argument_list|()
decl_stmt|;
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|requests
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|replies
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
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
name|number
condition|;
name|i
operator|++
control|)
block|{
name|requests
operator|.
name|add
argument_list|(
literal|"request"
operator|+
name|i
argument_list|)
expr_stmt|;
name|replies
operator|.
name|add
argument_list|(
literal|"reply"
operator|+
name|i
argument_list|)
expr_stmt|;
block|}
specifier|final
name|Group
name|group1
init|=
operator|new
name|Group
argument_list|(
name|connection1
argument_list|,
literal|"group1"
argument_list|)
decl_stmt|;
specifier|final
name|AtomicBoolean
name|finished
init|=
operator|new
name|AtomicBoolean
argument_list|()
decl_stmt|;
name|group1
operator|.
name|addGroupMessageListener
argument_list|(
operator|new
name|GroupMessageListener
argument_list|()
block|{
specifier|public
name|void
name|messageDelivered
parameter_list|(
name|Member
name|sender
parameter_list|,
name|String
name|replyId
parameter_list|,
name|Object
name|message
parameter_list|)
block|{
if|if
condition|(
operator|!
name|replies
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|String
name|reply
init|=
name|replies
operator|.
name|remove
argument_list|(
literal|0
argument_list|)
decl_stmt|;
try|try
block|{
name|group1
operator|.
name|sendMessageResponse
argument_list|(
name|sender
argument_list|,
name|replyId
argument_list|,
name|reply
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|JMSException
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
argument_list|)
expr_stmt|;
name|group1
operator|.
name|start
argument_list|()
expr_stmt|;
specifier|final
name|Group
name|group2
init|=
operator|new
name|Group
argument_list|(
name|connection2
argument_list|,
literal|"group2"
argument_list|)
decl_stmt|;
name|group2
operator|.
name|setMinimumGroupSize
argument_list|(
literal|2
argument_list|)
expr_stmt|;
name|group2
operator|.
name|addGroupMessageListener
argument_list|(
operator|new
name|GroupMessageListener
argument_list|()
block|{
specifier|public
name|void
name|messageDelivered
parameter_list|(
name|Member
name|sender
parameter_list|,
name|String
name|replyId
parameter_list|,
name|Object
name|message
parameter_list|)
block|{
if|if
condition|(
operator|!
name|requests
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|String
name|request
init|=
name|requests
operator|.
name|remove
argument_list|(
literal|0
argument_list|)
decl_stmt|;
try|try
block|{
name|group2
operator|.
name|sendMessage
argument_list|(
name|sender
argument_list|,
name|request
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|JMSException
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
else|else
block|{
synchronized|synchronized
init|(
name|finished
init|)
block|{
name|finished
operator|.
name|set
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|finished
operator|.
name|notifyAll
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
argument_list|)
expr_stmt|;
name|group2
operator|.
name|start
argument_list|()
expr_stmt|;
name|Member
name|member1
init|=
name|group2
operator|.
name|getMemberByName
argument_list|(
literal|"group1"
argument_list|)
decl_stmt|;
name|group2
operator|.
name|sendMessage
argument_list|(
name|member1
argument_list|,
name|requests
operator|.
name|remove
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
synchronized|synchronized
init|(
name|finished
init|)
block|{
if|if
condition|(
operator|!
name|finished
operator|.
name|get
argument_list|()
condition|)
block|{
name|finished
operator|.
name|wait
argument_list|(
literal|10000
argument_list|)
expr_stmt|;
block|}
block|}
name|assertTrue
argument_list|(
name|finished
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|group1
operator|.
name|stop
argument_list|()
expr_stmt|;
name|group2
operator|.
name|stop
argument_list|()
expr_stmt|;
name|connection1
operator|.
name|close
argument_list|()
expr_stmt|;
name|connection2
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
specifier|protected
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|broker
operator|==
literal|null
condition|)
block|{
name|broker
operator|=
name|createBroker
argument_list|()
expr_stmt|;
block|}
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
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
name|broker
operator|!=
literal|null
condition|)
block|{
name|broker
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
block|}
specifier|protected
name|ActiveMQConnectionFactory
name|createConnectionFactory
parameter_list|()
throws|throws
name|Exception
block|{
name|ActiveMQConnectionFactory
name|cf
init|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
name|ActiveMQConnection
operator|.
name|DEFAULT_BROKER_URL
argument_list|)
decl_stmt|;
return|return
name|cf
return|;
block|}
specifier|protected
name|BrokerService
name|createBroker
parameter_list|()
throws|throws
name|Exception
block|{
name|BrokerService
name|answer
init|=
operator|new
name|BrokerService
argument_list|()
decl_stmt|;
name|configureBroker
argument_list|(
name|answer
argument_list|)
expr_stmt|;
name|answer
operator|.
name|start
argument_list|()
expr_stmt|;
return|return
name|answer
return|;
block|}
specifier|protected
name|void
name|configureBroker
parameter_list|(
name|BrokerService
name|answer
parameter_list|)
throws|throws
name|Exception
block|{
name|answer
operator|.
name|setPersistent
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|answer
operator|.
name|addConnector
argument_list|(
name|bindAddress
argument_list|)
expr_stmt|;
name|answer
operator|.
name|setDeleteAllMessagesOnStartup
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

