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
name|camel
operator|.
name|component
package|;
end_package

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
name|InterruptedIOException
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
name|AtomicReference
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
name|kaha
operator|.
name|impl
operator|.
name|async
operator|.
name|AsyncDataManager
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
name|kaha
operator|.
name|impl
operator|.
name|async
operator|.
name|Location
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
name|ByteSequence
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|camel
operator|.
name|CamelExchangeException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|camel
operator|.
name|Consumer
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|camel
operator|.
name|Exchange
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|camel
operator|.
name|Processor
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|camel
operator|.
name|Producer
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|camel
operator|.
name|RuntimeCamelException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|camel
operator|.
name|impl
operator|.
name|DefaultConsumer
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|camel
operator|.
name|impl
operator|.
name|DefaultEndpoint
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|camel
operator|.
name|impl
operator|.
name|DefaultProducer
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
name|JournalEndpoint
extends|extends
name|DefaultEndpoint
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
name|JournalEndpoint
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|File
name|directory
decl_stmt|;
specifier|private
specifier|final
name|AtomicReference
argument_list|<
name|DefaultConsumer
argument_list|>
name|consumer
init|=
operator|new
name|AtomicReference
argument_list|<
name|DefaultConsumer
argument_list|>
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|Object
name|activationMutex
init|=
operator|new
name|Object
argument_list|()
decl_stmt|;
specifier|private
name|int
name|referenceCount
decl_stmt|;
specifier|private
name|AsyncDataManager
name|dataManager
decl_stmt|;
specifier|private
name|Thread
name|thread
decl_stmt|;
specifier|private
name|Location
name|lastReadLocation
decl_stmt|;
specifier|private
name|long
name|idleDelay
init|=
literal|1000
decl_stmt|;
specifier|private
name|boolean
name|syncProduce
init|=
literal|true
decl_stmt|;
specifier|private
name|boolean
name|syncConsume
decl_stmt|;
specifier|public
name|JournalEndpoint
parameter_list|(
name|String
name|uri
parameter_list|,
name|JournalComponent
name|journalComponent
parameter_list|,
name|File
name|directory
parameter_list|)
block|{
name|super
argument_list|(
name|uri
argument_list|,
name|journalComponent
operator|.
name|getCamelContext
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|directory
operator|=
name|directory
expr_stmt|;
block|}
specifier|public
name|JournalEndpoint
parameter_list|(
name|String
name|endpointUri
parameter_list|,
name|File
name|directory
parameter_list|)
block|{
name|super
argument_list|(
name|endpointUri
argument_list|)
expr_stmt|;
name|this
operator|.
name|directory
operator|=
name|directory
expr_stmt|;
block|}
specifier|public
name|boolean
name|isSingleton
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
specifier|public
name|File
name|getDirectory
parameter_list|()
block|{
return|return
name|directory
return|;
block|}
specifier|public
name|Consumer
name|createConsumer
parameter_list|(
name|Processor
name|processor
parameter_list|)
throws|throws
name|Exception
block|{
return|return
operator|new
name|DefaultConsumer
argument_list|(
name|this
argument_list|,
name|processor
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|void
name|start
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|start
argument_list|()
expr_stmt|;
name|activateConsumer
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|stop
parameter_list|()
throws|throws
name|Exception
block|{
name|deactivateConsumer
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|super
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
block|}
return|;
block|}
specifier|protected
name|void
name|decrementReference
parameter_list|()
throws|throws
name|IOException
block|{
synchronized|synchronized
init|(
name|activationMutex
init|)
block|{
name|referenceCount
operator|--
expr_stmt|;
if|if
condition|(
name|referenceCount
operator|==
literal|0
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Closing data manager: "
operator|+
name|directory
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Last mark at: "
operator|+
name|lastReadLocation
argument_list|)
expr_stmt|;
name|dataManager
operator|.
name|close
argument_list|()
expr_stmt|;
name|dataManager
operator|=
literal|null
expr_stmt|;
block|}
block|}
block|}
specifier|protected
name|void
name|incrementReference
parameter_list|()
throws|throws
name|IOException
block|{
synchronized|synchronized
init|(
name|activationMutex
init|)
block|{
name|referenceCount
operator|++
expr_stmt|;
if|if
condition|(
name|referenceCount
operator|==
literal|1
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Opening data manager: "
operator|+
name|directory
argument_list|)
expr_stmt|;
name|dataManager
operator|=
operator|new
name|AsyncDataManager
argument_list|()
expr_stmt|;
name|dataManager
operator|.
name|setDirectory
argument_list|(
name|directory
argument_list|)
expr_stmt|;
name|dataManager
operator|.
name|start
argument_list|()
expr_stmt|;
name|lastReadLocation
operator|=
name|dataManager
operator|.
name|getMark
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Last mark at: "
operator|+
name|lastReadLocation
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|protected
name|void
name|deactivateConsumer
parameter_list|(
name|DefaultConsumer
name|consumer
parameter_list|)
throws|throws
name|IOException
block|{
synchronized|synchronized
init|(
name|activationMutex
init|)
block|{
if|if
condition|(
name|this
operator|.
name|consumer
operator|.
name|get
argument_list|()
operator|!=
name|consumer
condition|)
block|{
throw|throw
operator|new
name|RuntimeCamelException
argument_list|(
literal|"Consumer was not active."
argument_list|)
throw|;
block|}
name|this
operator|.
name|consumer
operator|.
name|set
argument_list|(
literal|null
argument_list|)
expr_stmt|;
try|try
block|{
name|thread
operator|.
name|join
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|InterruptedIOException
argument_list|()
throw|;
block|}
name|decrementReference
argument_list|()
expr_stmt|;
block|}
block|}
specifier|protected
name|void
name|activateConsumer
parameter_list|(
name|DefaultConsumer
name|consumer
parameter_list|)
throws|throws
name|IOException
block|{
synchronized|synchronized
init|(
name|activationMutex
init|)
block|{
if|if
condition|(
name|this
operator|.
name|consumer
operator|.
name|get
argument_list|()
operator|!=
literal|null
condition|)
block|{
throw|throw
operator|new
name|RuntimeCamelException
argument_list|(
literal|"Consumer already active: journal endpoints only support 1 active consumer"
argument_list|)
throw|;
block|}
name|incrementReference
argument_list|()
expr_stmt|;
name|this
operator|.
name|consumer
operator|.
name|set
argument_list|(
name|consumer
argument_list|)
expr_stmt|;
name|thread
operator|=
operator|new
name|Thread
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
name|dispatchToConsumer
argument_list|()
expr_stmt|;
block|}
block|}
expr_stmt|;
name|thread
operator|.
name|setName
argument_list|(
literal|"Dipatch thread: "
operator|+
name|getEndpointUri
argument_list|()
argument_list|)
expr_stmt|;
name|thread
operator|.
name|setDaemon
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|thread
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
block|}
specifier|protected
name|void
name|dispatchToConsumer
parameter_list|()
block|{
try|try
block|{
name|DefaultConsumer
name|consumer
decl_stmt|;
while|while
condition|(
operator|(
name|consumer
operator|=
name|this
operator|.
name|consumer
operator|.
name|get
argument_list|()
operator|)
operator|!=
literal|null
condition|)
block|{
comment|// See if there is a new record to process
name|Location
name|location
init|=
name|dataManager
operator|.
name|getNextLocation
argument_list|(
name|lastReadLocation
argument_list|)
decl_stmt|;
if|if
condition|(
name|location
operator|!=
literal|null
condition|)
block|{
comment|// Send it on.
name|ByteSequence
name|read
init|=
name|dataManager
operator|.
name|read
argument_list|(
name|location
argument_list|)
decl_stmt|;
name|Exchange
name|exchange
init|=
name|createExchange
argument_list|()
decl_stmt|;
name|exchange
operator|.
name|getIn
argument_list|()
operator|.
name|setBody
argument_list|(
name|read
argument_list|)
expr_stmt|;
name|exchange
operator|.
name|getIn
argument_list|()
operator|.
name|setHeader
argument_list|(
literal|"journal"
argument_list|,
name|getEndpointUri
argument_list|()
argument_list|)
expr_stmt|;
name|exchange
operator|.
name|getIn
argument_list|()
operator|.
name|setHeader
argument_list|(
literal|"location"
argument_list|,
name|location
argument_list|)
expr_stmt|;
name|consumer
operator|.
name|getProcessor
argument_list|()
operator|.
name|process
argument_list|(
name|exchange
argument_list|)
expr_stmt|;
comment|// Setting the mark makes the data manager forget about
comment|// everything
comment|// before that record.
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Consumed record at: "
operator|+
name|location
argument_list|)
expr_stmt|;
block|}
name|dataManager
operator|.
name|setMark
argument_list|(
name|location
argument_list|,
name|syncConsume
argument_list|)
expr_stmt|;
name|lastReadLocation
operator|=
name|location
expr_stmt|;
block|}
else|else
block|{
comment|// Avoid a tight CPU loop if there is no new record to read.
name|LOG
operator|.
name|debug
argument_list|(
literal|"Sleeping due to no records being available."
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
name|idleDelay
argument_list|)
expr_stmt|;
block|}
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
block|}
specifier|public
name|Producer
name|createProducer
parameter_list|()
throws|throws
name|Exception
block|{
return|return
operator|new
name|DefaultProducer
argument_list|(
name|this
argument_list|)
block|{
specifier|public
name|void
name|process
parameter_list|(
name|Exchange
name|exchange
parameter_list|)
throws|throws
name|Exception
block|{
name|incrementReference
argument_list|()
expr_stmt|;
try|try
block|{
name|ByteSequence
name|body
init|=
name|exchange
operator|.
name|getIn
argument_list|()
operator|.
name|getBody
argument_list|(
name|ByteSequence
operator|.
name|class
argument_list|)
decl_stmt|;
if|if
condition|(
name|body
operator|==
literal|null
condition|)
block|{
name|byte
index|[]
name|bytes
init|=
name|exchange
operator|.
name|getIn
argument_list|()
operator|.
name|getBody
argument_list|(
name|byte
index|[]
operator|.
expr|class
argument_list|)
decl_stmt|;
if|if
condition|(
name|bytes
operator|!=
literal|null
condition|)
block|{
name|body
operator|=
operator|new
name|ByteSequence
argument_list|(
name|bytes
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|body
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|CamelExchangeException
argument_list|(
literal|"In body message could not be converted to a ByteSequence or a byte array."
argument_list|,
name|exchange
argument_list|)
throw|;
block|}
name|dataManager
operator|.
name|write
argument_list|(
name|body
argument_list|,
name|syncProduce
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|decrementReference
argument_list|()
expr_stmt|;
block|}
block|}
block|}
return|;
block|}
specifier|public
name|boolean
name|isSyncConsume
parameter_list|()
block|{
return|return
name|syncConsume
return|;
block|}
specifier|public
name|void
name|setSyncConsume
parameter_list|(
name|boolean
name|syncConsume
parameter_list|)
block|{
name|this
operator|.
name|syncConsume
operator|=
name|syncConsume
expr_stmt|;
block|}
specifier|public
name|boolean
name|isSyncProduce
parameter_list|()
block|{
return|return
name|syncProduce
return|;
block|}
specifier|public
name|void
name|setSyncProduce
parameter_list|(
name|boolean
name|syncProduce
parameter_list|)
block|{
name|this
operator|.
name|syncProduce
operator|=
name|syncProduce
expr_stmt|;
block|}
name|boolean
name|isOpen
parameter_list|()
block|{
synchronized|synchronized
init|(
name|activationMutex
init|)
block|{
return|return
name|referenceCount
operator|>
literal|0
return|;
block|}
block|}
block|}
end_class

end_unit

