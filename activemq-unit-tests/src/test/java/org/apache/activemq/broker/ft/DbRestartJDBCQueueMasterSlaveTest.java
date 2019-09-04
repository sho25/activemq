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
name|ft
package|;
end_package

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|PreparedStatement
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|ResultSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|SQLException
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
name|Destination
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
name|javax
operator|.
name|jms
operator|.
name|Message
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
name|javax
operator|.
name|jms
operator|.
name|TransactionRolledBackException
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
name|ActiveMQMessage
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
name|MessageId
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
name|store
operator|.
name|jdbc
operator|.
name|JDBCPersistenceAdapter
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|derby
operator|.
name|jdbc
operator|.
name|EmbeddedDataSource
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

begin_class
specifier|public
class|class
name|DbRestartJDBCQueueMasterSlaveTest
extends|extends
name|JDBCQueueMasterSlaveTest
block|{
specifier|private
specifier|static
specifier|final
specifier|transient
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|DbRestartJDBCQueueMasterSlaveTest
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|protected
name|void
name|messageSent
parameter_list|()
throws|throws
name|Exception
block|{
name|verifyExpectedBroker
argument_list|(
name|inflightMessageCount
argument_list|)
expr_stmt|;
if|if
condition|(
operator|++
name|inflightMessageCount
operator|==
name|failureCount
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"STOPPING DB!@!!!!"
argument_list|)
expr_stmt|;
specifier|final
name|EmbeddedDataSource
name|ds
init|=
operator|(
operator|(
name|SyncCreateDataSource
operator|)
name|getExistingDataSource
argument_list|()
operator|)
operator|.
name|getDelegate
argument_list|()
decl_stmt|;
name|ds
operator|.
name|setShutdownDatabase
argument_list|(
literal|"shutdown"
argument_list|)
expr_stmt|;
name|ds
operator|.
name|setCreateDatabase
argument_list|(
literal|"not_any_more"
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"DB STOPPED!@!!!!"
argument_list|)
expr_stmt|;
name|Thread
name|dbRestartThread
init|=
operator|new
name|Thread
argument_list|(
literal|"db-re-start-thread"
argument_list|)
block|{
specifier|public
name|void
name|run
parameter_list|()
block|{
name|delayTillRestartRequired
argument_list|()
expr_stmt|;
name|ds
operator|.
name|setShutdownDatabase
argument_list|(
literal|"false"
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"DB RESTARTED!@!!!!"
argument_list|)
expr_stmt|;
block|}
block|}
decl_stmt|;
name|dbRestartThread
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
name|verifyExpectedBroker
argument_list|(
name|inflightMessageCount
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|void
name|verifyExpectedBroker
parameter_list|(
name|int
name|inflightMessageCount
parameter_list|)
block|{
if|if
condition|(
name|inflightMessageCount
operator|==
literal|0
condition|)
block|{
name|assertEquals
argument_list|(
literal|"connected to master"
argument_list|,
name|master
operator|.
name|getBrokerName
argument_list|()
argument_list|,
operator|(
operator|(
name|ActiveMQConnection
operator|)
name|sendConnection
operator|)
operator|.
name|getBrokerName
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|inflightMessageCount
operator|==
name|failureCount
operator|+
literal|10
condition|)
block|{
name|assertEquals
argument_list|(
literal|"connected to slave, count:"
operator|+
name|inflightMessageCount
argument_list|,
name|slave
operator|.
name|get
argument_list|()
operator|.
name|getBrokerName
argument_list|()
argument_list|,
operator|(
operator|(
name|ActiveMQConnection
operator|)
name|sendConnection
operator|)
operator|.
name|getBrokerName
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
specifier|protected
name|void
name|delayTillRestartRequired
parameter_list|()
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Waiting for master broker to Stop"
argument_list|)
expr_stmt|;
name|master
operator|.
name|waitUntilStopped
argument_list|()
expr_stmt|;
block|}
specifier|protected
name|void
name|sendToProducer
parameter_list|(
name|MessageProducer
name|producer
parameter_list|,
name|Destination
name|producerDestination
parameter_list|,
name|Message
name|message
parameter_list|)
throws|throws
name|JMSException
block|{
name|producer
operator|.
name|send
argument_list|(
name|producerDestination
argument_list|,
name|message
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|Session
name|createReceiveSession
parameter_list|(
name|Connection
name|receiveConnection
parameter_list|)
throws|throws
name|Exception
block|{
return|return
name|receiveConnection
operator|.
name|createSession
argument_list|(
literal|true
argument_list|,
name|Session
operator|.
name|SESSION_TRANSACTED
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|consumeMessage
parameter_list|(
name|Message
name|message
parameter_list|,
name|List
argument_list|<
name|Message
argument_list|>
name|messageList
parameter_list|)
block|{
try|try
block|{
name|receiveSession
operator|.
name|commit
argument_list|()
expr_stmt|;
name|super
operator|.
name|consumeMessage
argument_list|(
name|message
argument_list|,
name|messageList
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|JMSException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Failed to commit message receipt: "
operator|+
name|message
argument_list|,
name|e
argument_list|)
expr_stmt|;
try|try
block|{
name|receiveSession
operator|.
name|rollback
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|JMSException
name|ignored
parameter_list|)
block|{             }
if|if
condition|(
name|e
operator|instanceof
name|TransactionRolledBackException
condition|)
block|{
name|TransactionRolledBackException
name|transactionRolledBackException
init|=
operator|(
name|TransactionRolledBackException
operator|)
name|e
decl_stmt|;
if|if
condition|(
name|transactionRolledBackException
operator|.
name|getMessage
argument_list|()
operator|.
name|indexOf
argument_list|(
literal|"in doubt"
argument_list|)
operator|!=
operator|-
literal|1
condition|)
block|{
comment|// failover chucked bc there is a missing reply to a commit.
comment|// failover is involved b/c the store exception is handled broker side and the client just
comment|// sees a disconnect (socket.close()).
comment|// If the client needs to be aware of the failure then it should not use IOExceptionHandler
comment|// so that the exception will propagate back
comment|// for this test case:
comment|// the commit may have got there and the reply is lost "or" the commit may be lost.
comment|// so we may or may not get a resend.
comment|//
comment|// At the application level we need to determine if the message is there or not which is not trivial
comment|// for this test we assert received == sent
comment|// so we need to know whether the message will be replayed.
comment|// we can ask the store b/c we know it is jdbc - guess we could go through a destination
comment|// message store interface also or use jmx
name|java
operator|.
name|sql
operator|.
name|Connection
name|dbConnection
init|=
literal|null
decl_stmt|;
try|try
block|{
name|ActiveMQMessage
name|mqMessage
init|=
operator|(
name|ActiveMQMessage
operator|)
name|message
decl_stmt|;
name|MessageId
name|id
init|=
name|mqMessage
operator|.
name|getMessageId
argument_list|()
decl_stmt|;
name|dbConnection
operator|=
name|sharedDs
operator|.
name|getConnection
argument_list|()
expr_stmt|;
name|PreparedStatement
name|s
init|=
name|dbConnection
operator|.
name|prepareStatement
argument_list|(
name|findStatement
argument_list|)
decl_stmt|;
name|s
operator|.
name|setString
argument_list|(
literal|1
argument_list|,
name|id
operator|.
name|getProducerId
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|s
operator|.
name|setLong
argument_list|(
literal|2
argument_list|,
name|id
operator|.
name|getProducerSequenceId
argument_list|()
argument_list|)
expr_stmt|;
name|ResultSet
name|rs
init|=
name|s
operator|.
name|executeQuery
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|rs
operator|.
name|next
argument_list|()
condition|)
block|{
comment|// message is gone, so lets count it as consumed
name|LOG
operator|.
name|info
argument_list|(
literal|"On TransactionRolledBackException we know that the ack/commit got there b/c message is gone so we count it: "
operator|+
name|mqMessage
argument_list|)
expr_stmt|;
name|super
operator|.
name|consumeMessage
argument_list|(
name|message
argument_list|,
name|messageList
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"On TransactionRolledBackException we know that the ack/commit was lost so we expect a replay of: "
operator|+
name|mqMessage
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|dbe
parameter_list|)
block|{
name|dbe
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
try|try
block|{
name|dbConnection
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SQLException
name|e1
parameter_list|)
block|{
name|e1
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
block|}
block|}
end_class

end_unit

