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
name|perf
package|;
end_package

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
name|ConnectionFactory
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

begin_comment
comment|/**  * @version $Revision: 1.3 $  */
end_comment

begin_class
specifier|public
class|class
name|PerfProducer
implements|implements
name|Runnable
block|{
specifier|protected
name|Connection
name|connection
decl_stmt|;
specifier|protected
name|MessageProducer
name|producer
decl_stmt|;
specifier|protected
name|PerfRate
name|rate
init|=
operator|new
name|PerfRate
argument_list|()
decl_stmt|;
specifier|private
name|byte
index|[]
name|payload
decl_stmt|;
specifier|private
name|Session
name|session
decl_stmt|;
specifier|private
specifier|final
name|CountDownLatch
name|stopped
init|=
operator|new
name|CountDownLatch
argument_list|(
literal|1
argument_list|)
decl_stmt|;
specifier|private
name|boolean
name|running
decl_stmt|;
specifier|public
name|PerfProducer
parameter_list|(
name|ConnectionFactory
name|fac
parameter_list|,
name|Destination
name|dest
parameter_list|,
name|byte
index|[]
name|palyload
parameter_list|)
throws|throws
name|JMSException
block|{
name|connection
operator|=
name|fac
operator|.
name|createConnection
argument_list|()
expr_stmt|;
name|session
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
name|producer
operator|=
name|session
operator|.
name|createProducer
argument_list|(
name|dest
argument_list|)
expr_stmt|;
name|this
operator|.
name|payload
operator|=
name|palyload
expr_stmt|;
block|}
specifier|public
name|void
name|setDeliveryMode
parameter_list|(
name|int
name|mode
parameter_list|)
throws|throws
name|JMSException
block|{
name|producer
operator|.
name|setDeliveryMode
argument_list|(
name|mode
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|shutDown
parameter_list|()
throws|throws
name|JMSException
block|{
name|connection
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
specifier|public
name|PerfRate
name|getRate
parameter_list|()
block|{
return|return
name|rate
return|;
block|}
specifier|public
specifier|synchronized
name|void
name|start
parameter_list|()
throws|throws
name|JMSException
block|{
if|if
condition|(
operator|!
name|running
condition|)
block|{
name|rate
operator|.
name|reset
argument_list|()
expr_stmt|;
name|running
operator|=
literal|true
expr_stmt|;
name|connection
operator|.
name|start
argument_list|()
expr_stmt|;
name|Thread
name|t
init|=
operator|new
name|Thread
argument_list|(
name|this
argument_list|)
decl_stmt|;
name|t
operator|.
name|setName
argument_list|(
literal|"Producer"
argument_list|)
expr_stmt|;
name|t
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|stop
parameter_list|()
throws|throws
name|JMSException
throws|,
name|InterruptedException
block|{
synchronized|synchronized
init|(
name|this
init|)
block|{
name|running
operator|=
literal|false
expr_stmt|;
block|}
name|stopped
operator|.
name|await
argument_list|(
literal|1
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
expr_stmt|;
name|connection
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
specifier|public
specifier|synchronized
name|boolean
name|isRunning
parameter_list|()
block|{
return|return
name|running
return|;
block|}
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
block|{
while|while
condition|(
name|isRunning
argument_list|()
condition|)
block|{
name|BytesMessage
name|msg
decl_stmt|;
name|msg
operator|=
name|session
operator|.
name|createBytesMessage
argument_list|()
expr_stmt|;
name|msg
operator|.
name|writeBytes
argument_list|(
name|payload
argument_list|)
expr_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|msg
argument_list|)
expr_stmt|;
name|rate
operator|.
name|increment
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
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|stopped
operator|.
name|countDown
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

