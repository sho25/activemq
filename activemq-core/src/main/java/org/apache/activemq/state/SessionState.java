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
name|state
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
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
name|ConsumerId
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
name|ConsumerInfo
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
name|ProducerId
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
name|ProducerInfo
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
name|SessionInfo
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
name|ConcurrentHashMap
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

begin_class
specifier|public
class|class
name|SessionState
block|{
specifier|final
name|SessionInfo
name|info
decl_stmt|;
specifier|public
specifier|final
name|ConcurrentHashMap
name|producers
init|=
operator|new
name|ConcurrentHashMap
argument_list|()
decl_stmt|;
specifier|public
specifier|final
name|ConcurrentHashMap
name|consumers
init|=
operator|new
name|ConcurrentHashMap
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|AtomicBoolean
name|shutdown
init|=
operator|new
name|AtomicBoolean
argument_list|(
literal|false
argument_list|)
decl_stmt|;
specifier|public
name|SessionState
parameter_list|(
name|SessionInfo
name|info
parameter_list|)
block|{
name|this
operator|.
name|info
operator|=
name|info
expr_stmt|;
block|}
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|info
operator|.
name|toString
argument_list|()
return|;
block|}
specifier|public
name|void
name|addProducer
parameter_list|(
name|ProducerInfo
name|info
parameter_list|)
block|{
name|checkShutdown
argument_list|()
expr_stmt|;
name|producers
operator|.
name|put
argument_list|(
name|info
operator|.
name|getProducerId
argument_list|()
argument_list|,
operator|new
name|ProducerState
argument_list|(
name|info
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|ProducerState
name|removeProducer
parameter_list|(
name|ProducerId
name|id
parameter_list|)
block|{
return|return
operator|(
name|ProducerState
operator|)
name|producers
operator|.
name|remove
argument_list|(
name|id
argument_list|)
return|;
block|}
specifier|public
name|void
name|addConsumer
parameter_list|(
name|ConsumerInfo
name|info
parameter_list|)
block|{
name|checkShutdown
argument_list|()
expr_stmt|;
name|consumers
operator|.
name|put
argument_list|(
name|info
operator|.
name|getConsumerId
argument_list|()
argument_list|,
operator|new
name|ConsumerState
argument_list|(
name|info
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|ConsumerState
name|removeConsumer
parameter_list|(
name|ConsumerId
name|id
parameter_list|)
block|{
return|return
operator|(
name|ConsumerState
operator|)
name|consumers
operator|.
name|remove
argument_list|(
name|id
argument_list|)
return|;
block|}
specifier|public
name|SessionInfo
name|getInfo
parameter_list|()
block|{
return|return
name|info
return|;
block|}
specifier|public
name|Set
name|getConsumerIds
parameter_list|()
block|{
return|return
name|consumers
operator|.
name|keySet
argument_list|()
return|;
block|}
specifier|public
name|Set
name|getProducerIds
parameter_list|()
block|{
return|return
name|producers
operator|.
name|keySet
argument_list|()
return|;
block|}
specifier|public
name|Collection
name|getProducerStates
parameter_list|()
block|{
return|return
name|producers
operator|.
name|values
argument_list|()
return|;
block|}
specifier|public
name|ProducerState
name|getProducerState
parameter_list|(
name|ProducerId
name|producerId
parameter_list|)
block|{
return|return
operator|(
name|ProducerState
operator|)
name|producers
operator|.
name|get
argument_list|(
name|producerId
argument_list|)
return|;
block|}
specifier|public
name|Collection
name|getConsumerStates
parameter_list|()
block|{
return|return
name|consumers
operator|.
name|values
argument_list|()
return|;
block|}
specifier|public
name|ConsumerState
name|getConsumerState
parameter_list|(
name|ConsumerId
name|consumerId
parameter_list|)
block|{
return|return
operator|(
name|ConsumerState
operator|)
name|consumers
operator|.
name|get
argument_list|(
name|consumerId
argument_list|)
return|;
block|}
specifier|private
name|void
name|checkShutdown
parameter_list|()
block|{
if|if
condition|(
name|shutdown
operator|.
name|get
argument_list|()
condition|)
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Disposed"
argument_list|)
throw|;
block|}
specifier|public
name|void
name|shutdown
parameter_list|()
block|{
name|shutdown
operator|.
name|set
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

