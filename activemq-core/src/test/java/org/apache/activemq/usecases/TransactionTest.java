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
name|java
operator|.
name|util
operator|.
name|Date
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
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
name|ActiveMQQueue
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

begin_comment
comment|/**  * @author pragmasoft  *   */
end_comment

begin_class
specifier|public
specifier|final
class|class
name|TransactionTest
extends|extends
name|TestCase
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
name|TransactionTest
operator|.
name|class
argument_list|)
decl_stmt|;
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
name|latch
operator|.
name|countDown
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"consumer received message :"
operator|+
name|receivedText
argument_list|)
expr_stmt|;
name|consumerSession
operator|.
name|commit
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"committed transaction"
argument_list|)
expr_stmt|;
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
name|LOG
operator|.
name|info
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
name|LOG
operator|.
name|info
argument_list|(
name|e1
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|e1
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
name|LOG
operator|.
name|info
argument_list|(
name|e
operator|.
name|toString
argument_list|()
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
literal|"Hello, "
operator|+
operator|new
name|Date
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
name|LOG
operator|.
name|info
argument_list|(
literal|"producer sent message :"
operator|+
name|tm
operator|.
name|getText
argument_list|()
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
name|LOG
operator|.
name|info
argument_list|(
literal|"Waiting for latch"
argument_list|)
expr_stmt|;
name|latch
operator|.
name|await
argument_list|(
literal|2
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|receivedText
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"test completed, destination="
operator|+
name|receivedText
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

