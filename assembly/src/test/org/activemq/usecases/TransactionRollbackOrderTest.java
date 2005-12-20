begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**   *   * Copyright 2004 Protique Ltd  *   * Licensed under the Apache License, Version 2.0 (the "License");   * you may not use this file except in compliance with the License.   * You may obtain a copy of the License at   *   * http://www.apache.org/licenses/LICENSE-2.0  *   * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,   * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.   * See the License for the specific language governing permissions and   * limitations under the License.   *   **/
end_comment

begin_package
package|package
name|org
operator|.
name|activemq
operator|.
name|usecases
package|;
end_package

begin_import
import|import
name|edu
operator|.
name|emory
operator|.
name|mathcs
operator|.
name|backport
operator|.
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|CountDownLatch
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
name|activemq
operator|.
name|ActiveMQConnectionFactory
import|;
end_import

begin_import
import|import
name|org
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
name|activemq
operator|.
name|command
operator|.
name|ActiveMQTopic
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
name|MessageConsumer
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|MessageListener
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
name|TextMessage
import|;
end_import

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

begin_comment
comment|/**  * Test case for AMQ-268  *  * @author Paul Smith  * @version $Revision: 1.1 $  */
end_comment

begin_class
specifier|public
specifier|final
class|class
name|TransactionRollbackOrderTest
extends|extends
name|TestCase
block|{
specifier|private
specifier|volatile
name|String
name|receivedText
decl_stmt|;
specifier|private
name|Session
name|producerSession
decl_stmt|;
specifier|private
name|Session
name|consumerSession
decl_stmt|;
specifier|private
name|Destination
name|queue
decl_stmt|;
specifier|private
name|MessageProducer
name|producer
decl_stmt|;
specifier|private
name|MessageConsumer
name|consumer
decl_stmt|;
specifier|private
name|Connection
name|connection
decl_stmt|;
specifier|private
name|CountDownLatch
name|latch
init|=
operator|new
name|CountDownLatch
argument_list|(
literal|1
argument_list|)
decl_stmt|;
specifier|private
name|int
name|NUM_MESSAGES
init|=
literal|5
decl_stmt|;
specifier|private
name|List
name|msgSent
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
specifier|private
name|List
name|msgCommitted
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
specifier|private
name|List
name|msgRolledBack
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
specifier|private
name|List
name|msgRedelivered
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
specifier|public
name|void
name|testTransaction
parameter_list|()
throws|throws
name|Exception
block|{
name|ActiveMQConnectionFactory
name|factory
init|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
literal|"vm://localhost?broker.persistent=false"
argument_list|)
decl_stmt|;
name|connection
operator|=
name|factory
operator|.
name|createConnection
argument_list|()
expr_stmt|;
name|queue
operator|=
operator|new
name|ActiveMQQueue
argument_list|(
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|"."
operator|+
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|producerSession
operator|=
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
expr_stmt|;
name|consumerSession
operator|=
name|connection
operator|.
name|createSession
argument_list|(
literal|true
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|producer
operator|=
name|producerSession
operator|.
name|createProducer
argument_list|(
name|queue
argument_list|)
expr_stmt|;
name|consumer
operator|=
name|consumerSession
operator|.
name|createConsumer
argument_list|(
name|queue
argument_list|)
expr_stmt|;
name|consumer
operator|.
name|setMessageListener
argument_list|(
operator|new
name|MessageListener
argument_list|()
block|{
name|int
name|msgCount
init|=
literal|0
decl_stmt|;
name|int
name|msgCommittedCount
init|=
literal|0
decl_stmt|;
specifier|public
name|void
name|onMessage
parameter_list|(
name|Message
name|m
parameter_list|)
block|{
try|try
block|{
name|msgCount
operator|++
expr_stmt|;
name|TextMessage
name|tm
init|=
operator|(
name|TextMessage
operator|)
name|m
decl_stmt|;
name|receivedText
operator|=
name|tm
operator|.
name|getText
argument_list|()
expr_stmt|;
if|if
condition|(
name|tm
operator|.
name|getJMSRedelivered
argument_list|()
condition|)
block|{
name|msgRedelivered
operator|.
name|add
argument_list|(
name|receivedText
argument_list|)
expr_stmt|;
block|}
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"consumer received message: "
operator|+
name|receivedText
operator|+
operator|(
name|tm
operator|.
name|getJMSRedelivered
argument_list|()
condition|?
literal|" ** Redelivered **"
else|:
literal|""
operator|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|msgCount
operator|==
literal|3
condition|)
block|{
name|msgRolledBack
operator|.
name|add
argument_list|(
name|receivedText
argument_list|)
expr_stmt|;
name|consumerSession
operator|.
name|rollback
argument_list|()
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"[msg: "
operator|+
name|receivedText
operator|+
literal|"] ** rolled back **"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|msgCommittedCount
operator|++
expr_stmt|;
name|msgCommitted
operator|.
name|add
argument_list|(
name|receivedText
argument_list|)
expr_stmt|;
name|consumerSession
operator|.
name|commit
argument_list|()
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"[msg: "
operator|+
name|receivedText
operator|+
literal|"] committed transaction "
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|msgCommittedCount
operator|==
name|NUM_MESSAGES
condition|)
block|{
name|latch
operator|.
name|countDown
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|JMSException
name|e
parameter_list|)
block|{
try|try
block|{
name|consumerSession
operator|.
name|rollback
argument_list|()
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"rolled back transaction"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|JMSException
name|e1
parameter_list|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|e1
argument_list|)
expr_stmt|;
name|e1
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|e
argument_list|)
expr_stmt|;
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
name|connection
operator|.
name|start
argument_list|()
expr_stmt|;
name|TextMessage
name|tm
init|=
literal|null
decl_stmt|;
try|try
block|{
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<=
name|NUM_MESSAGES
condition|;
name|i
operator|++
control|)
block|{
name|tm
operator|=
name|producerSession
operator|.
name|createTextMessage
argument_list|()
expr_stmt|;
name|tm
operator|.
name|setText
argument_list|(
literal|"Hello "
operator|+
name|i
argument_list|)
expr_stmt|;
name|msgSent
operator|.
name|add
argument_list|(
name|tm
operator|.
name|getText
argument_list|()
argument_list|)
expr_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|tm
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"producer sent message: "
operator|+
name|tm
operator|.
name|getText
argument_list|()
argument_list|)
expr_stmt|;
block|}
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
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Waiting for latch"
argument_list|)
expr_stmt|;
name|latch
operator|.
name|await
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|msgRolledBack
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|msgRedelivered
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"msg RolledBack = "
operator|+
name|msgRolledBack
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"msg Redelivered = "
operator|+
name|msgRedelivered
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|msgRolledBack
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|,
name|msgRedelivered
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|NUM_MESSAGES
argument_list|,
name|msgSent
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|NUM_MESSAGES
argument_list|,
name|msgCommitted
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|msgSent
argument_list|,
name|msgCommitted
argument_list|)
expr_stmt|;
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
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Closing the connection"
argument_list|)
expr_stmt|;
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
block|}
end_class

end_unit

