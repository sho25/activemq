begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR ONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
import|import
name|java
operator|.
name|io
operator|.
name|File
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
name|TimeUnit
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
name|BrokerFactory
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
name|store
operator|.
name|kahadb
operator|.
name|KahaDBPersistenceAdapter
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
name|Wait
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|After
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Before
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
name|DurableSubSelectorDelayTest
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
name|DurableSubSelectorDelayTest
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|long
name|RUNTIME
init|=
literal|3
operator|*
literal|60
operator|*
literal|1000
decl_stmt|;
specifier|private
name|BrokerService
name|broker
decl_stmt|;
specifier|private
name|ActiveMQTopic
name|topic
decl_stmt|;
annotation|@
name|Test
specifier|public
name|void
name|testProcess
parameter_list|()
throws|throws
name|Exception
block|{
name|MsgProducer
name|msgProducer
init|=
operator|new
name|MsgProducer
argument_list|()
decl_stmt|;
name|msgProducer
operator|.
name|start
argument_list|()
expr_stmt|;
name|DurableSubscriber
name|subscribers
index|[]
init|=
operator|new
name|DurableSubscriber
index|[
literal|10
index|]
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
name|subscribers
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|subscribers
index|[
name|i
index|]
operator|=
operator|new
name|DurableSubscriber
argument_list|(
name|i
argument_list|)
expr_stmt|;
name|subscribers
index|[
name|i
index|]
operator|.
name|process
argument_list|()
expr_stmt|;
block|}
comment|// wait for server to finish
name|msgProducer
operator|.
name|join
argument_list|()
expr_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|subscribers
operator|.
name|length
condition|;
name|j
operator|++
control|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Unsubscribing subscriber "
operator|+
name|subscribers
index|[
name|j
index|]
argument_list|)
expr_stmt|;
comment|// broker.getAdminView().destroyDurableSubscriber(clientID,
comment|// Client.SUBSCRIPTION_NAME);
name|subscribers
index|[
name|j
index|]
operator|.
name|unsubscribe
argument_list|()
expr_stmt|;
block|}
comment|// allow the clean up thread time to run
name|TimeUnit
operator|.
name|MINUTES
operator|.
name|sleep
argument_list|(
literal|2
argument_list|)
expr_stmt|;
specifier|final
name|KahaDBPersistenceAdapter
name|pa
init|=
operator|(
name|KahaDBPersistenceAdapter
operator|)
name|broker
operator|.
name|getPersistenceAdapter
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
literal|"only one journal file should be left "
argument_list|,
name|Wait
operator|.
name|waitFor
argument_list|(
operator|new
name|Wait
operator|.
name|Condition
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|isSatisified
parameter_list|()
throws|throws
name|Exception
block|{
return|return
name|pa
operator|.
name|getStore
argument_list|()
operator|.
name|getJournal
argument_list|()
operator|.
name|getFileMap
argument_list|()
operator|.
name|size
argument_list|()
operator|==
literal|1
return|;
block|}
block|}
argument_list|,
name|TimeUnit
operator|.
name|MINUTES
operator|.
name|toMillis
argument_list|(
literal|2
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"DONE."
argument_list|)
expr_stmt|;
block|}
comment|/**      * Message Producer      */
specifier|final
class|class
name|MsgProducer
extends|extends
name|Thread
block|{
specifier|final
name|String
name|url
init|=
literal|"vm://"
operator|+
name|DurableSubSelectorDelayTest
operator|.
name|getName
argument_list|()
decl_stmt|;
specifier|final
name|ConnectionFactory
name|cf
init|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
name|url
argument_list|)
decl_stmt|;
name|int
name|transRover
init|=
literal|0
decl_stmt|;
name|int
name|messageRover
init|=
literal|0
decl_stmt|;
specifier|public
name|MsgProducer
parameter_list|()
block|{
name|super
argument_list|(
literal|"MsgProducer"
argument_list|)
expr_stmt|;
name|setDaemon
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
name|long
name|endTime
init|=
name|RUNTIME
operator|+
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
try|try
block|{
while|while
condition|(
name|endTime
operator|>
name|System
operator|.
name|currentTimeMillis
argument_list|()
condition|)
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|400
argument_list|)
expr_stmt|;
name|send
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|(
name|System
operator|.
name|out
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
specifier|public
name|void
name|send
parameter_list|()
throws|throws
name|JMSException
block|{
name|int
name|trans
init|=
operator|++
name|transRover
decl_stmt|;
name|boolean
name|relevantTrans
init|=
literal|true
decl_stmt|;
name|int
name|count
init|=
literal|40
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Sending Trans[id="
operator|+
name|trans
operator|+
literal|", count="
operator|+
name|count
operator|+
literal|"]"
argument_list|)
expr_stmt|;
name|Connection
name|con
init|=
name|cf
operator|.
name|createConnection
argument_list|()
decl_stmt|;
name|Session
name|sess
init|=
name|con
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
name|MessageProducer
name|prod
init|=
name|sess
operator|.
name|createProducer
argument_list|(
literal|null
argument_list|)
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
name|count
condition|;
name|i
operator|++
control|)
block|{
name|Message
name|message
init|=
name|sess
operator|.
name|createMessage
argument_list|()
decl_stmt|;
name|message
operator|.
name|setIntProperty
argument_list|(
literal|"ID"
argument_list|,
operator|++
name|messageRover
argument_list|)
expr_stmt|;
name|message
operator|.
name|setIntProperty
argument_list|(
literal|"TRANS"
argument_list|,
name|trans
argument_list|)
expr_stmt|;
name|message
operator|.
name|setBooleanProperty
argument_list|(
literal|"RELEVANT"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|prod
operator|.
name|send
argument_list|(
name|topic
argument_list|,
name|message
argument_list|)
expr_stmt|;
block|}
name|Message
name|message
init|=
name|sess
operator|.
name|createMessage
argument_list|()
decl_stmt|;
name|message
operator|.
name|setIntProperty
argument_list|(
literal|"ID"
argument_list|,
operator|++
name|messageRover
argument_list|)
expr_stmt|;
name|message
operator|.
name|setIntProperty
argument_list|(
literal|"TRANS"
argument_list|,
name|trans
argument_list|)
expr_stmt|;
name|message
operator|.
name|setBooleanProperty
argument_list|(
literal|"COMMIT"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|message
operator|.
name|setBooleanProperty
argument_list|(
literal|"RELEVANT"
argument_list|,
name|relevantTrans
argument_list|)
expr_stmt|;
name|prod
operator|.
name|send
argument_list|(
name|topic
argument_list|,
name|message
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Committed Trans[id="
operator|+
name|trans
operator|+
literal|", count="
operator|+
name|count
operator|+
literal|"], ID="
operator|+
name|messageRover
argument_list|)
expr_stmt|;
name|sess
operator|.
name|close
argument_list|()
expr_stmt|;
name|con
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**      * Consumes massages from a durable subscription. Goes online/offline      * periodically. Checks the incoming messages against the sent messages of      * the server.      */
specifier|private
specifier|final
class|class
name|DurableSubscriber
block|{
name|String
name|url
init|=
literal|"tcp://localhost:61656"
decl_stmt|;
specifier|final
name|ConnectionFactory
name|cf
init|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
name|url
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|String
name|subName
decl_stmt|;
specifier|private
specifier|final
name|int
name|id
decl_stmt|;
specifier|private
specifier|final
name|String
name|conClientId
decl_stmt|;
specifier|private
specifier|final
name|String
name|selector
decl_stmt|;
specifier|public
name|DurableSubscriber
parameter_list|(
name|int
name|id
parameter_list|)
throws|throws
name|JMSException
block|{
name|this
operator|.
name|id
operator|=
name|id
expr_stmt|;
name|conClientId
operator|=
literal|"cli"
operator|+
name|id
expr_stmt|;
name|subName
operator|=
literal|"subscription"
operator|+
name|id
expr_stmt|;
name|selector
operator|=
literal|"RELEVANT = true"
expr_stmt|;
block|}
specifier|private
name|void
name|process
parameter_list|()
throws|throws
name|JMSException
block|{
name|long
name|end
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|+
literal|20000
decl_stmt|;
name|int
name|transCount
init|=
literal|0
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
name|toString
argument_list|()
operator|+
literal|" ONLINE."
argument_list|)
expr_stmt|;
name|Connection
name|con
init|=
name|openConnection
argument_list|()
decl_stmt|;
name|Session
name|sess
init|=
name|con
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
name|consumer
init|=
name|sess
operator|.
name|createDurableSubscriber
argument_list|(
name|topic
argument_list|,
name|subName
argument_list|,
name|selector
argument_list|,
literal|false
argument_list|)
decl_stmt|;
comment|//MessageConsumer consumer = sess.createDurableSubscriber(topic,SUBSCRIPTION_NAME);
try|try
block|{
do|do
block|{
name|long
name|max
init|=
name|end
operator|-
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
if|if
condition|(
name|max
operator|<=
literal|0
condition|)
block|{
break|break;
block|}
name|Message
name|message
init|=
name|consumer
operator|.
name|receive
argument_list|(
name|max
argument_list|)
decl_stmt|;
if|if
condition|(
name|message
operator|==
literal|null
condition|)
continue|continue;
name|LOG
operator|.
name|info
argument_list|(
literal|"Received Trans[id="
operator|+
name|message
operator|.
name|getIntProperty
argument_list|(
literal|"TRANS"
argument_list|)
operator|+
literal|", count="
operator|+
name|transCount
operator|+
literal|"] in "
operator|+
name|this
operator|+
literal|"."
argument_list|)
expr_stmt|;
block|}
do|while
condition|(
literal|true
condition|)
do|;
block|}
finally|finally
block|{
name|sess
operator|.
name|close
argument_list|()
expr_stmt|;
name|con
operator|.
name|close
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
name|toString
argument_list|()
operator|+
literal|" OFFLINE."
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|Connection
name|openConnection
parameter_list|()
throws|throws
name|JMSException
block|{
name|Connection
name|con
init|=
name|cf
operator|.
name|createConnection
argument_list|()
decl_stmt|;
name|con
operator|.
name|setClientID
argument_list|(
name|conClientId
argument_list|)
expr_stmt|;
name|con
operator|.
name|start
argument_list|()
expr_stmt|;
return|return
name|con
return|;
block|}
specifier|private
name|void
name|unsubscribe
parameter_list|()
throws|throws
name|JMSException
block|{
name|Connection
name|con
init|=
name|openConnection
argument_list|()
decl_stmt|;
name|Session
name|session
init|=
name|con
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
name|unsubscribe
argument_list|(
name|subName
argument_list|)
expr_stmt|;
name|session
operator|.
name|close
argument_list|()
expr_stmt|;
name|con
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"DurableSubscriber[id="
operator|+
name|id
operator|+
literal|"]"
return|;
block|}
block|}
annotation|@
name|Before
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|topic
operator|=
operator|new
name|ActiveMQTopic
argument_list|(
literal|"TopicT"
argument_list|)
expr_stmt|;
name|startBroker
argument_list|()
expr_stmt|;
block|}
annotation|@
name|After
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
name|destroyBroker
argument_list|()
expr_stmt|;
block|}
specifier|private
name|void
name|startBroker
parameter_list|()
throws|throws
name|Exception
block|{
name|startBroker
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|startBroker
parameter_list|(
name|boolean
name|deleteAllMessages
parameter_list|)
throws|throws
name|Exception
block|{
if|if
condition|(
name|broker
operator|!=
literal|null
condition|)
return|return;
name|broker
operator|=
name|BrokerFactory
operator|.
name|createBroker
argument_list|(
literal|"broker:(vm://"
operator|+
name|getName
argument_list|()
operator|+
literal|")"
argument_list|)
expr_stmt|;
name|broker
operator|.
name|setBrokerName
argument_list|(
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|broker
operator|.
name|setAdvisorySupport
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|broker
operator|.
name|setDeleteAllMessagesOnStartup
argument_list|(
name|deleteAllMessages
argument_list|)
expr_stmt|;
name|File
name|kahadbData
init|=
operator|new
name|File
argument_list|(
literal|"activemq-data/"
operator|+
name|getName
argument_list|()
operator|+
literal|"-kahadb"
argument_list|)
decl_stmt|;
if|if
condition|(
name|deleteAllMessages
condition|)
name|delete
argument_list|(
name|kahadbData
argument_list|)
expr_stmt|;
name|broker
operator|.
name|setPersistent
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|KahaDBPersistenceAdapter
name|kahadb
init|=
operator|new
name|KahaDBPersistenceAdapter
argument_list|()
decl_stmt|;
name|kahadb
operator|.
name|setDirectory
argument_list|(
name|kahadbData
argument_list|)
expr_stmt|;
name|kahadb
operator|.
name|setJournalMaxFileLength
argument_list|(
literal|500
operator|*
literal|1024
argument_list|)
expr_stmt|;
name|broker
operator|.
name|setPersistenceAdapter
argument_list|(
name|kahadb
argument_list|)
expr_stmt|;
name|broker
operator|.
name|addConnector
argument_list|(
literal|"tcp://localhost:61656"
argument_list|)
expr_stmt|;
name|broker
operator|.
name|getSystemUsage
argument_list|()
operator|.
name|getMemoryUsage
argument_list|()
operator|.
name|setLimit
argument_list|(
literal|256
operator|*
literal|1024
operator|*
literal|1024
argument_list|)
expr_stmt|;
name|broker
operator|.
name|getSystemUsage
argument_list|()
operator|.
name|getTempUsage
argument_list|()
operator|.
name|setLimit
argument_list|(
literal|256
operator|*
literal|1024
operator|*
literal|1024
argument_list|)
expr_stmt|;
name|broker
operator|.
name|getSystemUsage
argument_list|()
operator|.
name|getStoreUsage
argument_list|()
operator|.
name|setLimit
argument_list|(
literal|256
operator|*
literal|1024
operator|*
literal|1024
argument_list|)
expr_stmt|;
name|broker
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
specifier|protected
specifier|static
name|String
name|getName
parameter_list|()
block|{
return|return
literal|"DurableSubSelectorDelayTest"
return|;
block|}
specifier|private
specifier|static
name|boolean
name|delete
parameter_list|(
name|File
name|path
parameter_list|)
block|{
if|if
condition|(
name|path
operator|==
literal|null
condition|)
return|return
literal|true
return|;
if|if
condition|(
name|path
operator|.
name|isDirectory
argument_list|()
condition|)
block|{
for|for
control|(
name|File
name|file
range|:
name|path
operator|.
name|listFiles
argument_list|()
control|)
block|{
name|delete
argument_list|(
name|file
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|path
operator|.
name|delete
argument_list|()
return|;
block|}
specifier|private
name|void
name|destroyBroker
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
return|return;
name|broker
operator|.
name|stop
argument_list|()
expr_stmt|;
name|broker
operator|=
literal|null
expr_stmt|;
block|}
block|}
end_class

end_unit

