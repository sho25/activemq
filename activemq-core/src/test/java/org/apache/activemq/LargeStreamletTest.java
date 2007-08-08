begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|activemq
package|;
end_package

begin_comment
comment|/**  *  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|Log
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|LogFactory
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|InputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|OutputStream
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
name|Destination
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
name|Assert
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

begin_comment
comment|/**  * @author rnewson  */
end_comment

begin_class
specifier|public
specifier|final
class|class
name|LargeStreamletTest
extends|extends
name|TestCase
block|{
specifier|private
specifier|static
specifier|final
name|Log
name|log
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|LargeStreamletTest
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|BROKER_URL
init|=
literal|"vm://localhost?broker.persistent=false"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|int
name|BUFFER_SIZE
init|=
literal|1
operator|*
literal|1024
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|int
name|MESSAGE_COUNT
init|=
literal|10
operator|*
literal|1024
decl_stmt|;
specifier|private
name|AtomicInteger
name|totalRead
init|=
operator|new
name|AtomicInteger
argument_list|()
decl_stmt|;
specifier|private
name|AtomicInteger
name|totalWritten
init|=
operator|new
name|AtomicInteger
argument_list|()
decl_stmt|;
specifier|private
name|AtomicBoolean
name|stopThreads
init|=
operator|new
name|AtomicBoolean
argument_list|(
literal|false
argument_list|)
decl_stmt|;
specifier|protected
name|Exception
name|writerException
decl_stmt|;
specifier|protected
name|Exception
name|readerException
decl_stmt|;
specifier|public
name|void
name|testStreamlets
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|ActiveMQConnectionFactory
name|factory
init|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
name|BROKER_URL
argument_list|)
decl_stmt|;
specifier|final
name|ActiveMQConnection
name|connection
init|=
operator|(
name|ActiveMQConnection
operator|)
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
try|try
block|{
specifier|final
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
try|try
block|{
specifier|final
name|Destination
name|destination
init|=
name|session
operator|.
name|createQueue
argument_list|(
literal|"wibble"
argument_list|)
decl_stmt|;
specifier|final
name|Thread
name|readerThread
init|=
operator|new
name|Thread
argument_list|(
operator|new
name|Runnable
argument_list|()
block|{
specifier|public
name|void
name|run
parameter_list|()
block|{
name|totalRead
operator|.
name|set
argument_list|(
literal|0
argument_list|)
expr_stmt|;
try|try
block|{
specifier|final
name|InputStream
name|inputStream
init|=
name|connection
operator|.
name|createInputStream
argument_list|(
name|destination
argument_list|)
decl_stmt|;
try|try
block|{
name|int
name|read
decl_stmt|;
specifier|final
name|byte
index|[]
name|buf
init|=
operator|new
name|byte
index|[
name|BUFFER_SIZE
index|]
decl_stmt|;
while|while
condition|(
operator|!
name|stopThreads
operator|.
name|get
argument_list|()
operator|&&
operator|(
name|read
operator|=
name|inputStream
operator|.
name|read
argument_list|(
name|buf
argument_list|)
operator|)
operator|!=
operator|-
literal|1
condition|)
block|{
name|totalRead
operator|.
name|addAndGet
argument_list|(
name|read
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|inputStream
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|readerException
operator|=
name|e
expr_stmt|;
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|log
operator|.
name|info
argument_list|(
name|totalRead
operator|+
literal|" total bytes read."
argument_list|)
expr_stmt|;
block|}
block|}
block|}
argument_list|)
decl_stmt|;
specifier|final
name|Thread
name|writerThread
init|=
operator|new
name|Thread
argument_list|(
operator|new
name|Runnable
argument_list|()
block|{
specifier|private
specifier|final
name|Random
name|random
init|=
operator|new
name|Random
argument_list|()
decl_stmt|;
specifier|public
name|void
name|run
parameter_list|()
block|{
name|totalWritten
operator|.
name|set
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|int
name|count
init|=
name|MESSAGE_COUNT
decl_stmt|;
try|try
block|{
specifier|final
name|OutputStream
name|outputStream
init|=
name|connection
operator|.
name|createOutputStream
argument_list|(
name|destination
argument_list|)
decl_stmt|;
try|try
block|{
specifier|final
name|byte
index|[]
name|buf
init|=
operator|new
name|byte
index|[
name|BUFFER_SIZE
index|]
decl_stmt|;
name|random
operator|.
name|nextBytes
argument_list|(
name|buf
argument_list|)
expr_stmt|;
while|while
condition|(
name|count
operator|>
literal|0
operator|&&
operator|!
name|stopThreads
operator|.
name|get
argument_list|()
condition|)
block|{
name|outputStream
operator|.
name|write
argument_list|(
name|buf
argument_list|)
expr_stmt|;
name|totalWritten
operator|.
name|addAndGet
argument_list|(
name|buf
operator|.
name|length
argument_list|)
expr_stmt|;
name|count
operator|--
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|outputStream
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|writerException
operator|=
name|e
expr_stmt|;
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|log
operator|.
name|info
argument_list|(
name|totalWritten
operator|+
literal|" total bytes written."
argument_list|)
expr_stmt|;
block|}
block|}
block|}
argument_list|)
decl_stmt|;
name|readerThread
operator|.
name|start
argument_list|()
expr_stmt|;
name|writerThread
operator|.
name|start
argument_list|()
expr_stmt|;
comment|// Wait till reader is has finished receiving all the messages
comment|// or he has stopped
comment|// receiving messages.
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
name|int
name|lastRead
init|=
name|totalRead
operator|.
name|get
argument_list|()
decl_stmt|;
while|while
condition|(
name|readerThread
operator|.
name|isAlive
argument_list|()
condition|)
block|{
name|readerThread
operator|.
name|join
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
comment|// No progress?? then stop waiting..
if|if
condition|(
name|lastRead
operator|==
name|totalRead
operator|.
name|get
argument_list|()
condition|)
block|{
break|break;
block|}
name|lastRead
operator|=
name|totalRead
operator|.
name|get
argument_list|()
expr_stmt|;
block|}
name|stopThreads
operator|.
name|set
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Should not have received a reader exception"
argument_list|,
name|readerException
operator|==
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Should not have received a writer exception"
argument_list|,
name|writerException
operator|==
literal|null
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"Not all messages accounted for"
argument_list|,
name|totalWritten
operator|.
name|get
argument_list|()
argument_list|,
name|totalRead
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|session
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|connection
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

