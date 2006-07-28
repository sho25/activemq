begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  *  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|tool
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|PrintWriter
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
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
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
name|Random
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|BytesMessage
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
name|DeliveryMode
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
name|TimeUnit
import|;
end_import

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
name|atomic
operator|.
name|AtomicBoolean
import|;
end_import

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
name|atomic
operator|.
name|AtomicInteger
import|;
end_import

begin_comment
comment|/**  * @version $Revision$  */
end_comment

begin_class
specifier|public
class|class
name|AcidTestTool
extends|extends
name|TestCase
block|{
specifier|private
name|Random
name|random
init|=
operator|new
name|Random
argument_list|()
decl_stmt|;
specifier|private
name|byte
name|data
index|[]
decl_stmt|;
specifier|private
name|int
name|workerCount
init|=
literal|10
decl_stmt|;
specifier|private
name|PrintWriter
name|statWriter
decl_stmt|;
comment|// Worker configuration.
specifier|protected
name|int
name|recordSize
init|=
literal|1024
decl_stmt|;
specifier|protected
name|int
name|batchSize
init|=
literal|5
decl_stmt|;
specifier|protected
name|int
name|workerThinkTime
init|=
literal|500
decl_stmt|;
name|AtomicBoolean
name|ignoreJMSErrors
init|=
operator|new
name|AtomicBoolean
argument_list|(
literal|false
argument_list|)
decl_stmt|;
specifier|protected
name|Destination
name|target
decl_stmt|;
specifier|private
name|ActiveMQConnectionFactory
name|factory
decl_stmt|;
specifier|private
name|Connection
name|connection
decl_stmt|;
name|AtomicInteger
name|publishedBatches
init|=
operator|new
name|AtomicInteger
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|AtomicInteger
name|consumedBatches
init|=
operator|new
name|AtomicInteger
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|List
name|errors
init|=
name|Collections
operator|.
name|synchronizedList
argument_list|(
operator|new
name|ArrayList
argument_list|()
argument_list|)
decl_stmt|;
specifier|private
interface|interface
name|Worker
extends|extends
name|Runnable
block|{
specifier|public
name|boolean
name|waitForExit
parameter_list|(
name|long
name|i
parameter_list|)
throws|throws
name|InterruptedException
function_decl|;
block|}
specifier|private
specifier|final
class|class
name|ProducerWorker
implements|implements
name|Worker
block|{
name|Session
name|session
decl_stmt|;
specifier|private
name|MessageProducer
name|producer
decl_stmt|;
specifier|private
name|BytesMessage
name|message
decl_stmt|;
name|CountDownLatch
name|doneLatch
init|=
operator|new
name|CountDownLatch
argument_list|(
literal|1
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|String
name|workerId
decl_stmt|;
name|ProducerWorker
parameter_list|(
name|Session
name|session
parameter_list|,
name|String
name|workerId
parameter_list|)
throws|throws
name|JMSException
block|{
name|this
operator|.
name|session
operator|=
name|session
expr_stmt|;
name|this
operator|.
name|workerId
operator|=
name|workerId
expr_stmt|;
name|producer
operator|=
name|session
operator|.
name|createProducer
argument_list|(
name|target
argument_list|)
expr_stmt|;
name|producer
operator|.
name|setDeliveryMode
argument_list|(
name|DeliveryMode
operator|.
name|PERSISTENT
argument_list|)
expr_stmt|;
name|message
operator|=
name|session
operator|.
name|createBytesMessage
argument_list|()
expr_stmt|;
name|message
operator|.
name|setStringProperty
argument_list|(
literal|"workerId"
argument_list|,
name|workerId
argument_list|)
expr_stmt|;
name|message
operator|.
name|writeBytes
argument_list|(
name|data
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
block|{
for|for
control|(
name|int
name|batchId
init|=
literal|0
init|;
literal|true
condition|;
name|batchId
operator|++
control|)
block|{
comment|//				    System.out.println("Sending batch: "+workerId+" "+batchId);
for|for
control|(
name|int
name|msgId
init|=
literal|0
init|;
name|msgId
operator|<
name|batchSize
condition|;
name|msgId
operator|++
control|)
block|{
comment|// Sleep some random amount of time less than workerThinkTime
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
name|random
operator|.
name|nextInt
argument_list|(
name|workerThinkTime
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e1
parameter_list|)
block|{
return|return;
block|}
name|message
operator|.
name|setIntProperty
argument_list|(
literal|"batch-id"
argument_list|,
name|batchId
argument_list|)
expr_stmt|;
name|message
operator|.
name|setIntProperty
argument_list|(
literal|"msg-id"
argument_list|,
name|msgId
argument_list|)
expr_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|message
argument_list|)
expr_stmt|;
block|}
name|session
operator|.
name|commit
argument_list|()
expr_stmt|;
name|publishedBatches
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
comment|//				    System.out.println("Commited send batch: "+workerId+" "+batchId);
block|}
block|}
catch|catch
parameter_list|(
name|JMSException
name|e
parameter_list|)
block|{
if|if
condition|(
operator|!
name|ignoreJMSErrors
operator|.
name|get
argument_list|()
condition|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
name|errors
operator|.
name|add
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
return|return;
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
argument_list|()
expr_stmt|;
name|errors
operator|.
name|add
argument_list|(
name|e
argument_list|)
expr_stmt|;
return|return;
block|}
finally|finally
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Producer exiting."
argument_list|)
expr_stmt|;
name|doneLatch
operator|.
name|countDown
argument_list|()
expr_stmt|;
block|}
block|}
specifier|public
name|boolean
name|waitForExit
parameter_list|(
name|long
name|i
parameter_list|)
throws|throws
name|InterruptedException
block|{
return|return
name|doneLatch
operator|.
name|await
argument_list|(
name|i
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
return|;
block|}
block|}
specifier|private
specifier|final
class|class
name|ConsumerWorker
implements|implements
name|Worker
block|{
name|Session
name|session
decl_stmt|;
specifier|private
name|MessageConsumer
name|consumer
decl_stmt|;
specifier|private
specifier|final
name|long
name|timeout
decl_stmt|;
name|CountDownLatch
name|doneLatch
init|=
operator|new
name|CountDownLatch
argument_list|(
literal|1
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|String
name|workerId
decl_stmt|;
name|ConsumerWorker
parameter_list|(
name|Session
name|session
parameter_list|,
name|String
name|workerId
parameter_list|,
name|long
name|timeout
parameter_list|)
throws|throws
name|JMSException
block|{
name|this
operator|.
name|session
operator|=
name|session
expr_stmt|;
name|this
operator|.
name|workerId
operator|=
name|workerId
expr_stmt|;
name|this
operator|.
name|timeout
operator|=
name|timeout
expr_stmt|;
name|consumer
operator|=
name|session
operator|.
name|createConsumer
argument_list|(
name|target
argument_list|,
literal|"workerId='"
operator|+
name|workerId
operator|+
literal|"'"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
block|{
name|int
name|batchId
init|=
literal|0
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
for|for
control|(
name|int
name|msgId
init|=
literal|0
init|;
name|msgId
operator|<
name|batchSize
condition|;
name|msgId
operator|++
control|)
block|{
comment|// Sleep some random amount of time less than workerThinkTime
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
name|random
operator|.
name|nextInt
argument_list|(
name|workerThinkTime
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e1
parameter_list|)
block|{
return|return;
block|}
name|Message
name|message
init|=
name|consumer
operator|.
name|receive
argument_list|(
name|timeout
argument_list|)
decl_stmt|;
if|if
condition|(
name|msgId
operator|>
literal|0
condition|)
block|{
name|assertNotNull
argument_list|(
name|message
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|message
operator|.
name|getIntProperty
argument_list|(
literal|"batch-id"
argument_list|)
argument_list|,
name|batchId
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|message
operator|.
name|getIntProperty
argument_list|(
literal|"msg-id"
argument_list|)
argument_list|,
name|msgId
argument_list|)
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|message
operator|==
literal|null
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"At end of batch an don't have a next batch to process.  done."
argument_list|)
expr_stmt|;
return|return;
block|}
name|assertEquals
argument_list|(
name|msgId
argument_list|,
name|message
operator|.
name|getIntProperty
argument_list|(
literal|"msg-id"
argument_list|)
argument_list|)
expr_stmt|;
name|batchId
operator|=
name|message
operator|.
name|getIntProperty
argument_list|(
literal|"batch-id"
argument_list|)
expr_stmt|;
comment|//	    				    System.out.println("Receiving batch: "+workerId+" "+batchId);
block|}
block|}
name|session
operator|.
name|commit
argument_list|()
expr_stmt|;
name|consumedBatches
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
comment|//				    System.out.println("Commited receive batch: "+workerId+" "+batchId);
block|}
block|}
catch|catch
parameter_list|(
name|JMSException
name|e
parameter_list|)
block|{
if|if
condition|(
operator|!
name|ignoreJMSErrors
operator|.
name|get
argument_list|()
condition|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
name|errors
operator|.
name|add
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
return|return;
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
argument_list|()
expr_stmt|;
name|errors
operator|.
name|add
argument_list|(
name|e
argument_list|)
expr_stmt|;
return|return;
block|}
finally|finally
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Consumer exiting."
argument_list|)
expr_stmt|;
name|doneLatch
operator|.
name|countDown
argument_list|()
expr_stmt|;
block|}
block|}
specifier|public
name|boolean
name|waitForExit
parameter_list|(
name|long
name|i
parameter_list|)
throws|throws
name|InterruptedException
block|{
return|return
name|doneLatch
operator|.
name|await
argument_list|(
name|i
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
return|;
block|}
block|}
comment|/**      * @see junit.framework.TestCase#setUp()      */
specifier|protected
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|factory
operator|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
literal|"tcp://localhost:61616"
argument_list|)
expr_stmt|;
name|this
operator|.
name|target
operator|=
operator|new
name|ActiveMQQueue
argument_list|(
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
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
try|try
block|{
name|connection
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|ignore
parameter_list|)
block|{}
name|connection
operator|=
literal|null
expr_stmt|;
block|}
block|}
comment|/**      * @throws InterruptedException      * @throws JMSException      * @throws JMSException      *       */
specifier|private
name|void
name|reconnect
parameter_list|()
throws|throws
name|InterruptedException
throws|,
name|JMSException
block|{
if|if
condition|(
name|connection
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|connection
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|ignore
parameter_list|)
block|{}
name|connection
operator|=
literal|null
expr_stmt|;
block|}
name|long
name|reconnectDelay
init|=
literal|1000
decl_stmt|;
name|JMSException
name|lastError
init|=
literal|null
decl_stmt|;
while|while
condition|(
name|connection
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|reconnectDelay
operator|>
literal|1000
operator|*
literal|10
condition|)
block|{
name|reconnectDelay
operator|=
literal|1000
operator|*
literal|10
expr_stmt|;
block|}
try|try
block|{
name|connection
operator|=
name|factory
operator|.
name|createConnection
argument_list|()
expr_stmt|;
name|connection
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|JMSException
name|e
parameter_list|)
block|{
name|lastError
operator|=
name|e
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
name|reconnectDelay
argument_list|)
expr_stmt|;
name|reconnectDelay
operator|*=
literal|2
expr_stmt|;
block|}
block|}
block|}
comment|/**      * @throws Throwable       * @throws IOException      *       */
specifier|public
name|void
name|testAcidTransactions
parameter_list|()
throws|throws
name|Throwable
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Client threads write records using: Record Size: "
operator|+
name|recordSize
operator|+
literal|", Batch Size: "
operator|+
name|batchSize
operator|+
literal|", Worker Think Time: "
operator|+
name|workerThinkTime
argument_list|)
expr_stmt|;
comment|// Create the record and fill it with some values.
name|data
operator|=
operator|new
name|byte
index|[
name|recordSize
index|]
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|data
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|data
index|[
name|i
index|]
operator|=
operator|(
name|byte
operator|)
name|i
expr_stmt|;
block|}
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"=============================================="
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"===> Start the server now."
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"=============================================="
argument_list|)
expr_stmt|;
name|reconnect
argument_list|()
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Starting "
operator|+
name|workerCount
operator|+
literal|" Workers..."
argument_list|)
expr_stmt|;
name|ArrayList
name|workers
init|=
operator|new
name|ArrayList
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
name|workerCount
condition|;
name|i
operator|++
control|)
block|{
name|String
name|workerId
init|=
literal|"worker-"
operator|+
name|i
decl_stmt|;
name|Worker
name|w
init|=
operator|new
name|ConsumerWorker
argument_list|(
name|connection
operator|.
name|createSession
argument_list|(
literal|true
argument_list|,
name|Session
operator|.
name|SESSION_TRANSACTED
argument_list|)
argument_list|,
name|workerId
argument_list|,
literal|1000
operator|*
literal|5
argument_list|)
decl_stmt|;
name|workers
operator|.
name|add
argument_list|(
name|w
argument_list|)
expr_stmt|;
operator|new
name|Thread
argument_list|(
name|w
argument_list|,
literal|"Consumer:"
operator|+
name|workerId
argument_list|)
operator|.
name|start
argument_list|()
expr_stmt|;
name|w
operator|=
operator|new
name|ProducerWorker
argument_list|(
name|connection
operator|.
name|createSession
argument_list|(
literal|true
argument_list|,
name|Session
operator|.
name|SESSION_TRANSACTED
argument_list|)
argument_list|,
name|workerId
argument_list|)
expr_stmt|;
name|workers
operator|.
name|add
argument_list|(
name|w
argument_list|)
expr_stmt|;
operator|new
name|Thread
argument_list|(
name|w
argument_list|,
literal|"Producer:"
operator|+
name|workerId
argument_list|)
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Waiting for "
operator|+
operator|(
name|workerCount
operator|*
literal|10
operator|)
operator|+
literal|" batches to be delivered."
argument_list|)
expr_stmt|;
comment|//
comment|// Wait for about 5 batches of messages per worker to be consumed before restart.
comment|//
while|while
condition|(
name|publishedBatches
operator|.
name|get
argument_list|()
operator|<
name|workerCount
operator|*
literal|5
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Stats: Produced Batches: "
operator|+
name|this
operator|.
name|publishedBatches
operator|.
name|get
argument_list|()
operator|+
literal|", Consumed Batches: "
operator|+
name|this
operator|.
name|consumedBatches
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
block|}
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"=============================================="
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"===> Server is under load now.  Kill it!"
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"=============================================="
argument_list|)
expr_stmt|;
name|ignoreJMSErrors
operator|.
name|set
argument_list|(
literal|true
argument_list|)
expr_stmt|;
comment|// Wait for all the workers to finish.
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Waiting for all workers to exit due to server shutdown."
argument_list|)
expr_stmt|;
for|for
control|(
name|Iterator
name|iter
init|=
name|workers
operator|.
name|iterator
argument_list|()
init|;
name|iter
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|Worker
name|worker
init|=
operator|(
name|Worker
operator|)
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
while|while
condition|(
operator|!
name|worker
operator|.
name|waitForExit
argument_list|(
literal|1000
argument_list|)
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"=============================================="
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"===> Server is under load now.  Kill it!"
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"=============================================="
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Stats: Produced Batches: "
operator|+
name|this
operator|.
name|publishedBatches
operator|.
name|get
argument_list|()
operator|+
literal|", Consumed Batches: "
operator|+
name|this
operator|.
name|consumedBatches
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|workers
operator|.
name|clear
argument_list|()
expr_stmt|;
comment|// No errors should have occured so far.
if|if
condition|(
name|errors
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
throw|throw
operator|(
name|Throwable
operator|)
name|errors
operator|.
name|get
argument_list|(
literal|0
argument_list|)
throw|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"=============================================="
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"===> Start the server now."
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"=============================================="
argument_list|)
expr_stmt|;
name|reconnect
argument_list|()
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Restarted."
argument_list|)
expr_stmt|;
comment|// Validate the all transactions were commited as a uow.  Looking for partial commits.
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|workerCount
condition|;
name|i
operator|++
control|)
block|{
name|String
name|workerId
init|=
literal|"worker-"
operator|+
name|i
decl_stmt|;
name|Worker
name|w
init|=
operator|new
name|ConsumerWorker
argument_list|(
name|connection
operator|.
name|createSession
argument_list|(
literal|true
argument_list|,
name|Session
operator|.
name|SESSION_TRANSACTED
argument_list|)
argument_list|,
name|workerId
argument_list|,
literal|5
operator|*
literal|1000
argument_list|)
decl_stmt|;
name|workers
operator|.
name|add
argument_list|(
name|w
argument_list|)
expr_stmt|;
operator|new
name|Thread
argument_list|(
name|w
argument_list|,
literal|"Consumer:"
operator|+
name|workerId
argument_list|)
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Waiting for restarted consumers to finish consuming all messages.."
argument_list|)
expr_stmt|;
for|for
control|(
name|Iterator
name|iter
init|=
name|workers
operator|.
name|iterator
argument_list|()
init|;
name|iter
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|Worker
name|worker
init|=
operator|(
name|Worker
operator|)
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
while|while
condition|(
operator|!
name|worker
operator|.
name|waitForExit
argument_list|(
literal|1000
operator|*
literal|5
argument_list|)
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Waiting for restarted consumers to finish consuming all messages.."
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Stats: Produced Batches: "
operator|+
name|this
operator|.
name|publishedBatches
operator|.
name|get
argument_list|()
operator|+
literal|", Consumed Batches: "
operator|+
name|this
operator|.
name|consumedBatches
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|workers
operator|.
name|clear
argument_list|()
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Workers finished.."
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Stats: Produced Batches: "
operator|+
name|this
operator|.
name|publishedBatches
operator|.
name|get
argument_list|()
operator|+
literal|", Consumed Batches: "
operator|+
name|this
operator|.
name|consumedBatches
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|errors
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
throw|throw
operator|(
name|Throwable
operator|)
name|errors
operator|.
name|get
argument_list|(
literal|0
argument_list|)
throw|;
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
try|try
block|{
name|AcidTestTool
name|tool
init|=
operator|new
name|AcidTestTool
argument_list|()
decl_stmt|;
name|tool
operator|.
name|setUp
argument_list|()
expr_stmt|;
name|tool
operator|.
name|testAcidTransactions
argument_list|()
expr_stmt|;
name|tool
operator|.
name|tearDown
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Test Failed: "
operator|+
name|e
operator|.
name|getMessage
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
end_class

end_unit

