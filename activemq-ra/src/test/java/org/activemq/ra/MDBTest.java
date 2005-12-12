begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**   *   * Copyright 2005 LogicBlaze Inc.  *   * Licensed under the Apache License, Version 2.0 (the "License");   * you may not use this file except in compliance with the License.   * You may obtain a copy of the License at   *   * http://www.apache.org/licenses/LICENSE-2.0  *   * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,   * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.   * See the License for the specific language governing permissions and   * limitations under the License.   *   **/
end_comment

begin_package
package|package
name|org
operator|.
name|activemq
operator|.
name|ra
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ByteArrayOutputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|DataOutputStream
import|;
end_import

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
name|lang
operator|.
name|reflect
operator|.
name|Method
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Timer
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
name|Message
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
name|Queue
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
name|resource
operator|.
name|ResourceException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|resource
operator|.
name|spi
operator|.
name|BootstrapContext
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|resource
operator|.
name|spi
operator|.
name|UnavailableException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|resource
operator|.
name|spi
operator|.
name|XATerminator
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|resource
operator|.
name|spi
operator|.
name|endpoint
operator|.
name|MessageEndpoint
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|resource
operator|.
name|spi
operator|.
name|endpoint
operator|.
name|MessageEndpointFactory
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|resource
operator|.
name|spi
operator|.
name|work
operator|.
name|ExecutionContext
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|resource
operator|.
name|spi
operator|.
name|work
operator|.
name|Work
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|resource
operator|.
name|spi
operator|.
name|work
operator|.
name|WorkException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|resource
operator|.
name|spi
operator|.
name|work
operator|.
name|WorkListener
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|resource
operator|.
name|spi
operator|.
name|work
operator|.
name|WorkManager
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|transaction
operator|.
name|xa
operator|.
name|XAResource
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|transaction
operator|.
name|xa
operator|.
name|Xid
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

begin_class
specifier|public
class|class
name|MDBTest
extends|extends
name|TestCase
block|{
specifier|private
specifier|final
class|class
name|StubBootstrapContext
implements|implements
name|BootstrapContext
block|{
specifier|public
name|WorkManager
name|getWorkManager
parameter_list|()
block|{
return|return
operator|new
name|WorkManager
argument_list|()
block|{
specifier|public
name|void
name|doWork
parameter_list|(
name|Work
name|work
parameter_list|)
throws|throws
name|WorkException
block|{
operator|new
name|Thread
argument_list|(
name|work
argument_list|)
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|doWork
parameter_list|(
name|Work
name|work
parameter_list|,
name|long
name|arg1
parameter_list|,
name|ExecutionContext
name|arg2
parameter_list|,
name|WorkListener
name|arg3
parameter_list|)
throws|throws
name|WorkException
block|{
operator|new
name|Thread
argument_list|(
name|work
argument_list|)
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
specifier|public
name|long
name|startWork
parameter_list|(
name|Work
name|work
parameter_list|)
throws|throws
name|WorkException
block|{
operator|new
name|Thread
argument_list|(
name|work
argument_list|)
operator|.
name|start
argument_list|()
expr_stmt|;
return|return
literal|0
return|;
block|}
specifier|public
name|long
name|startWork
parameter_list|(
name|Work
name|work
parameter_list|,
name|long
name|arg1
parameter_list|,
name|ExecutionContext
name|arg2
parameter_list|,
name|WorkListener
name|arg3
parameter_list|)
throws|throws
name|WorkException
block|{
operator|new
name|Thread
argument_list|(
name|work
argument_list|)
operator|.
name|start
argument_list|()
expr_stmt|;
return|return
literal|0
return|;
block|}
specifier|public
name|void
name|scheduleWork
parameter_list|(
name|Work
name|work
parameter_list|)
throws|throws
name|WorkException
block|{
operator|new
name|Thread
argument_list|(
name|work
argument_list|)
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|scheduleWork
parameter_list|(
name|Work
name|work
parameter_list|,
name|long
name|arg1
parameter_list|,
name|ExecutionContext
name|arg2
parameter_list|,
name|WorkListener
name|arg3
parameter_list|)
throws|throws
name|WorkException
block|{
operator|new
name|Thread
argument_list|(
name|work
argument_list|)
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
block|}
return|;
block|}
specifier|public
name|XATerminator
name|getXATerminator
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
specifier|public
name|Timer
name|createTimer
parameter_list|()
throws|throws
name|UnavailableException
block|{
return|return
literal|null
return|;
block|}
block|}
specifier|public
class|class
name|StubMessageEndpoint
implements|implements
name|MessageEndpoint
implements|,
name|MessageListener
block|{
specifier|public
name|int
name|messageCount
decl_stmt|;
specifier|public
name|XAResource
name|xaresource
decl_stmt|;
specifier|public
name|Xid
name|xid
init|=
literal|null
decl_stmt|;
specifier|public
name|void
name|beforeDelivery
parameter_list|(
name|Method
name|method
parameter_list|)
throws|throws
name|NoSuchMethodException
throws|,
name|ResourceException
block|{
try|try
block|{
if|if
condition|(
name|xid
operator|==
literal|null
condition|)
name|xid
operator|=
name|createXid
argument_list|()
expr_stmt|;
name|xaresource
operator|.
name|start
argument_list|(
name|xid
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|ResourceException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
specifier|public
name|void
name|afterDelivery
parameter_list|()
throws|throws
name|ResourceException
block|{
try|try
block|{
name|xaresource
operator|.
name|end
argument_list|(
name|xid
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|xaresource
operator|.
name|prepare
argument_list|(
name|xid
argument_list|)
expr_stmt|;
name|xaresource
operator|.
name|commit
argument_list|(
name|xid
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|ResourceException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
specifier|public
name|void
name|release
parameter_list|()
block|{         }
specifier|public
name|void
name|onMessage
parameter_list|(
name|Message
name|message
parameter_list|)
block|{
name|messageCount
operator|++
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|testMessageDelivery
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
name|Connection
name|connection
init|=
name|factory
operator|.
name|createConnection
argument_list|()
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
literal|0
argument_list|)
decl_stmt|;
name|ActiveMQResourceAdapter
name|adapter
init|=
operator|new
name|ActiveMQResourceAdapter
argument_list|()
decl_stmt|;
name|adapter
operator|.
name|setServerUrl
argument_list|(
literal|"vm://localhost?broker.persistent=false"
argument_list|)
expr_stmt|;
name|adapter
operator|.
name|start
argument_list|(
operator|new
name|StubBootstrapContext
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|CountDownLatch
name|messageDelivered
init|=
operator|new
name|CountDownLatch
argument_list|(
literal|1
argument_list|)
decl_stmt|;
specifier|final
name|StubMessageEndpoint
name|endpoint
init|=
operator|new
name|StubMessageEndpoint
argument_list|()
block|{
specifier|public
name|void
name|onMessage
parameter_list|(
name|Message
name|message
parameter_list|)
block|{
name|super
operator|.
name|onMessage
argument_list|(
name|message
argument_list|)
expr_stmt|;
name|messageDelivered
operator|.
name|countDown
argument_list|()
expr_stmt|;
block|}
empty_stmt|;
block|}
decl_stmt|;
name|ActiveMQActivationSpec
name|activationSpec
init|=
operator|new
name|ActiveMQActivationSpec
argument_list|()
decl_stmt|;
name|activationSpec
operator|.
name|setDestinationType
argument_list|(
name|Queue
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|activationSpec
operator|.
name|setDestination
argument_list|(
literal|"TEST"
argument_list|)
expr_stmt|;
name|activationSpec
operator|.
name|setResourceAdapter
argument_list|(
name|adapter
argument_list|)
expr_stmt|;
name|activationSpec
operator|.
name|validate
argument_list|()
expr_stmt|;
name|MessageEndpointFactory
name|messageEndpointFactory
init|=
operator|new
name|MessageEndpointFactory
argument_list|()
block|{
specifier|public
name|MessageEndpoint
name|createEndpoint
parameter_list|(
name|XAResource
name|resource
parameter_list|)
throws|throws
name|UnavailableException
block|{
name|endpoint
operator|.
name|xaresource
operator|=
name|resource
expr_stmt|;
return|return
name|endpoint
return|;
block|}
specifier|public
name|boolean
name|isDeliveryTransacted
parameter_list|(
name|Method
name|method
parameter_list|)
throws|throws
name|NoSuchMethodException
block|{
return|return
literal|true
return|;
block|}
block|}
decl_stmt|;
comment|// Activate an Endpoint
name|adapter
operator|.
name|endpointActivation
argument_list|(
name|messageEndpointFactory
argument_list|,
name|activationSpec
argument_list|)
expr_stmt|;
comment|// Give endpoint a chance to setup and register its listeners
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{          }
comment|// Send the broker a message to that endpoint
name|MessageProducer
name|producer
init|=
name|session
operator|.
name|createProducer
argument_list|(
operator|new
name|ActiveMQQueue
argument_list|(
literal|"TEST"
argument_list|)
argument_list|)
decl_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|session
operator|.
name|createTextMessage
argument_list|(
literal|"Hello!"
argument_list|)
argument_list|)
expr_stmt|;
name|connection
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// Wait for the message to be delivered.
name|assertTrue
argument_list|(
name|messageDelivered
operator|.
name|await
argument_list|(
literal|5000
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
argument_list|)
expr_stmt|;
comment|// Shut the Endpoint down.
name|adapter
operator|.
name|endpointDeactivation
argument_list|(
name|messageEndpointFactory
argument_list|,
name|activationSpec
argument_list|)
expr_stmt|;
name|adapter
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
name|long
name|txGenerator
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
specifier|public
name|Xid
name|createXid
parameter_list|()
throws|throws
name|IOException
block|{
name|ByteArrayOutputStream
name|baos
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
name|DataOutputStream
name|os
init|=
operator|new
name|DataOutputStream
argument_list|(
name|baos
argument_list|)
decl_stmt|;
name|os
operator|.
name|writeLong
argument_list|(
operator|++
name|txGenerator
argument_list|)
expr_stmt|;
name|os
operator|.
name|close
argument_list|()
expr_stmt|;
specifier|final
name|byte
index|[]
name|bs
init|=
name|baos
operator|.
name|toByteArray
argument_list|()
decl_stmt|;
return|return
operator|new
name|Xid
argument_list|()
block|{
specifier|public
name|int
name|getFormatId
parameter_list|()
block|{
return|return
literal|86
return|;
block|}
specifier|public
name|byte
index|[]
name|getGlobalTransactionId
parameter_list|()
block|{
return|return
name|bs
return|;
block|}
specifier|public
name|byte
index|[]
name|getBranchQualifier
parameter_list|()
block|{
return|return
name|bs
return|;
block|}
block|}
return|;
block|}
block|}
end_class

end_unit

