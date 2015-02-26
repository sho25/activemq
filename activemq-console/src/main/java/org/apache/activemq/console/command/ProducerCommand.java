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
name|console
operator|.
name|command
package|;
end_package

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
name|command
operator|.
name|ActiveMQDestination
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
name|util
operator|.
name|IntrospectionSupport
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
name|util
operator|.
name|ProducerThread
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
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
name|Session
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|*
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
name|CountDownLatch
import|;
end_import

begin_class
specifier|public
class|class
name|ProducerCommand
extends|extends
name|AbstractCommand
block|{
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|ProducerCommand
operator|.
name|class
argument_list|)
decl_stmt|;
name|String
name|brokerUrl
init|=
name|ActiveMQConnectionFactory
operator|.
name|DEFAULT_BROKER_URL
decl_stmt|;
name|String
name|user
init|=
name|ActiveMQConnectionFactory
operator|.
name|DEFAULT_USER
decl_stmt|;
name|String
name|password
init|=
name|ActiveMQConnectionFactory
operator|.
name|DEFAULT_PASSWORD
decl_stmt|;
name|String
name|destination
init|=
literal|"queue://TEST"
decl_stmt|;
name|int
name|messageCount
init|=
literal|1000
decl_stmt|;
name|int
name|sleep
init|=
literal|0
decl_stmt|;
name|boolean
name|persistent
init|=
literal|true
decl_stmt|;
name|int
name|messageSize
init|=
literal|0
decl_stmt|;
name|int
name|textMessageSize
decl_stmt|;
name|long
name|msgTTL
init|=
literal|0L
decl_stmt|;
name|String
name|msgGroupID
init|=
literal|null
decl_stmt|;
name|int
name|transactionBatchSize
decl_stmt|;
specifier|private
name|int
name|parallelThreads
init|=
literal|1
decl_stmt|;
annotation|@
name|Override
specifier|protected
name|void
name|runTask
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|tokens
parameter_list|)
throws|throws
name|Exception
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Connecting to URL: "
operator|+
name|brokerUrl
operator|+
literal|" ("
operator|+
name|user
operator|+
literal|":"
operator|+
name|password
operator|+
literal|")"
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Producing messages to "
operator|+
name|destination
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Using "
operator|+
operator|(
name|persistent
condition|?
literal|"persistent"
else|:
literal|"non-persistent"
operator|)
operator|+
literal|" messages"
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Sleeping between sends "
operator|+
name|sleep
operator|+
literal|" ms"
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Running "
operator|+
name|parallelThreads
operator|+
literal|" parallel threads"
argument_list|)
expr_stmt|;
name|ActiveMQConnectionFactory
name|factory
init|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
name|brokerUrl
argument_list|)
decl_stmt|;
name|Connection
name|conn
init|=
literal|null
decl_stmt|;
try|try
block|{
name|conn
operator|=
name|factory
operator|.
name|createConnection
argument_list|(
name|user
argument_list|,
name|password
argument_list|)
expr_stmt|;
name|conn
operator|.
name|start
argument_list|()
expr_stmt|;
name|Session
name|sess
decl_stmt|;
if|if
condition|(
name|transactionBatchSize
operator|!=
literal|0
condition|)
block|{
name|sess
operator|=
name|conn
operator|.
name|createSession
argument_list|(
literal|true
argument_list|,
name|Session
operator|.
name|SESSION_TRANSACTED
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|sess
operator|=
name|conn
operator|.
name|createSession
argument_list|(
literal|false
argument_list|,
name|Session
operator|.
name|AUTO_ACKNOWLEDGE
argument_list|)
expr_stmt|;
block|}
name|CountDownLatch
name|active
init|=
operator|new
name|CountDownLatch
argument_list|(
name|parallelThreads
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<=
name|parallelThreads
condition|;
name|i
operator|++
control|)
block|{
name|ProducerThread
name|producer
init|=
operator|new
name|ProducerThread
argument_list|(
name|sess
argument_list|,
name|ActiveMQDestination
operator|.
name|createDestination
argument_list|(
name|destination
argument_list|,
name|ActiveMQDestination
operator|.
name|QUEUE_TYPE
argument_list|)
argument_list|)
decl_stmt|;
name|producer
operator|.
name|setName
argument_list|(
literal|"producer-"
operator|+
name|i
argument_list|)
expr_stmt|;
name|producer
operator|.
name|setMessageCount
argument_list|(
name|messageCount
argument_list|)
expr_stmt|;
name|producer
operator|.
name|setSleep
argument_list|(
name|sleep
argument_list|)
expr_stmt|;
name|producer
operator|.
name|setMsgTTL
argument_list|(
name|msgTTL
argument_list|)
expr_stmt|;
name|producer
operator|.
name|setPersistent
argument_list|(
name|persistent
argument_list|)
expr_stmt|;
name|producer
operator|.
name|setTransactionBatchSize
argument_list|(
name|transactionBatchSize
argument_list|)
expr_stmt|;
name|producer
operator|.
name|setMessageSize
argument_list|(
name|messageSize
argument_list|)
expr_stmt|;
name|producer
operator|.
name|setMsgGroupID
argument_list|(
name|msgGroupID
argument_list|)
expr_stmt|;
name|producer
operator|.
name|setTextMessageSize
argument_list|(
name|textMessageSize
argument_list|)
expr_stmt|;
name|producer
operator|.
name|setFinished
argument_list|(
name|active
argument_list|)
expr_stmt|;
name|producer
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
name|active
operator|.
name|await
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|conn
operator|!=
literal|null
condition|)
block|{
name|conn
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
specifier|public
name|String
name|getBrokerUrl
parameter_list|()
block|{
return|return
name|brokerUrl
return|;
block|}
specifier|public
name|void
name|setBrokerUrl
parameter_list|(
name|String
name|brokerUrl
parameter_list|)
block|{
name|this
operator|.
name|brokerUrl
operator|=
name|brokerUrl
expr_stmt|;
block|}
specifier|public
name|String
name|getDestination
parameter_list|()
block|{
return|return
name|destination
return|;
block|}
specifier|public
name|void
name|setDestination
parameter_list|(
name|String
name|destination
parameter_list|)
block|{
name|this
operator|.
name|destination
operator|=
name|destination
expr_stmt|;
block|}
specifier|public
name|int
name|getMessageCount
parameter_list|()
block|{
return|return
name|messageCount
return|;
block|}
specifier|public
name|void
name|setMessageCount
parameter_list|(
name|int
name|messageCount
parameter_list|)
block|{
name|this
operator|.
name|messageCount
operator|=
name|messageCount
expr_stmt|;
block|}
specifier|public
name|int
name|getSleep
parameter_list|()
block|{
return|return
name|sleep
return|;
block|}
specifier|public
name|void
name|setSleep
parameter_list|(
name|int
name|sleep
parameter_list|)
block|{
name|this
operator|.
name|sleep
operator|=
name|sleep
expr_stmt|;
block|}
specifier|public
name|boolean
name|isPersistent
parameter_list|()
block|{
return|return
name|persistent
return|;
block|}
specifier|public
name|void
name|setPersistent
parameter_list|(
name|boolean
name|persistent
parameter_list|)
block|{
name|this
operator|.
name|persistent
operator|=
name|persistent
expr_stmt|;
block|}
specifier|public
name|int
name|getMessageSize
parameter_list|()
block|{
return|return
name|messageSize
return|;
block|}
specifier|public
name|void
name|setMessageSize
parameter_list|(
name|int
name|messageSize
parameter_list|)
block|{
name|this
operator|.
name|messageSize
operator|=
name|messageSize
expr_stmt|;
block|}
specifier|public
name|int
name|getTextMessageSize
parameter_list|()
block|{
return|return
name|textMessageSize
return|;
block|}
specifier|public
name|void
name|setTextMessageSize
parameter_list|(
name|int
name|textMessageSize
parameter_list|)
block|{
name|this
operator|.
name|textMessageSize
operator|=
name|textMessageSize
expr_stmt|;
block|}
specifier|public
name|long
name|getMsgTTL
parameter_list|()
block|{
return|return
name|msgTTL
return|;
block|}
specifier|public
name|void
name|setMsgTTL
parameter_list|(
name|long
name|msgTTL
parameter_list|)
block|{
name|this
operator|.
name|msgTTL
operator|=
name|msgTTL
expr_stmt|;
block|}
specifier|public
name|String
name|getMsgGroupID
parameter_list|()
block|{
return|return
name|msgGroupID
return|;
block|}
specifier|public
name|void
name|setMsgGroupID
parameter_list|(
name|String
name|msgGroupID
parameter_list|)
block|{
name|this
operator|.
name|msgGroupID
operator|=
name|msgGroupID
expr_stmt|;
block|}
specifier|public
name|int
name|getTransactionBatchSize
parameter_list|()
block|{
return|return
name|transactionBatchSize
return|;
block|}
specifier|public
name|void
name|setTransactionBatchSize
parameter_list|(
name|int
name|transactionBatchSize
parameter_list|)
block|{
name|this
operator|.
name|transactionBatchSize
operator|=
name|transactionBatchSize
expr_stmt|;
block|}
specifier|public
name|String
name|getUser
parameter_list|()
block|{
return|return
name|user
return|;
block|}
specifier|public
name|void
name|setUser
parameter_list|(
name|String
name|user
parameter_list|)
block|{
name|this
operator|.
name|user
operator|=
name|user
expr_stmt|;
block|}
specifier|public
name|String
name|getPassword
parameter_list|()
block|{
return|return
name|password
return|;
block|}
specifier|public
name|void
name|setPassword
parameter_list|(
name|String
name|password
parameter_list|)
block|{
name|this
operator|.
name|password
operator|=
name|password
expr_stmt|;
block|}
specifier|public
name|int
name|getParallelThreads
parameter_list|()
block|{
return|return
name|parallelThreads
return|;
block|}
specifier|public
name|void
name|setParallelThreads
parameter_list|(
name|int
name|parallelThreads
parameter_list|)
block|{
name|this
operator|.
name|parallelThreads
operator|=
name|parallelThreads
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|printHelp
parameter_list|()
block|{
name|printHelpFromFile
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
literal|"producer"
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getOneLineDescription
parameter_list|()
block|{
return|return
literal|"Sends messages to the broker"
return|;
block|}
block|}
end_class

end_unit
