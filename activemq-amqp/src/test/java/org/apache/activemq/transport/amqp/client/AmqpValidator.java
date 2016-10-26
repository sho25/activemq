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
name|transport
operator|.
name|amqp
operator|.
name|client
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
name|qpid
operator|.
name|proton
operator|.
name|engine
operator|.
name|Connection
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|qpid
operator|.
name|proton
operator|.
name|engine
operator|.
name|Delivery
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|qpid
operator|.
name|proton
operator|.
name|engine
operator|.
name|Receiver
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|qpid
operator|.
name|proton
operator|.
name|engine
operator|.
name|Sender
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|qpid
operator|.
name|proton
operator|.
name|engine
operator|.
name|Session
import|;
end_import

begin_comment
comment|/**  * Abstract base for a validation hook that is used in tests to check the state  * of a remote resource after a variety of lifecycle events.  */
end_comment

begin_class
specifier|public
class|class
name|AmqpValidator
block|{
specifier|private
name|AtomicReference
argument_list|<
name|String
argument_list|>
name|errorMessage
init|=
operator|new
name|AtomicReference
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
specifier|public
name|void
name|inspectOpenedResource
parameter_list|(
name|Connection
name|connection
parameter_list|)
block|{      }
specifier|public
name|void
name|inspectOpenedResource
parameter_list|(
name|Session
name|session
parameter_list|)
block|{      }
specifier|public
name|void
name|inspectOpenedResource
parameter_list|(
name|Sender
name|sender
parameter_list|)
block|{      }
specifier|public
name|void
name|inspectOpenedResource
parameter_list|(
name|Receiver
name|receiver
parameter_list|)
block|{      }
specifier|public
name|void
name|inspectClosedResource
parameter_list|(
name|Connection
name|remoteConnection
parameter_list|)
block|{      }
specifier|public
name|void
name|inspectClosedResource
parameter_list|(
name|Session
name|session
parameter_list|)
block|{      }
specifier|public
name|void
name|inspectClosedResource
parameter_list|(
name|Sender
name|sender
parameter_list|)
block|{      }
specifier|public
name|void
name|inspectClosedResource
parameter_list|(
name|Receiver
name|receiver
parameter_list|)
block|{      }
specifier|public
name|void
name|inspectDetachedResource
parameter_list|(
name|Sender
name|sender
parameter_list|)
block|{      }
specifier|public
name|void
name|inspectDetachedResource
parameter_list|(
name|Receiver
name|receiver
parameter_list|)
block|{      }
specifier|public
name|void
name|inspectDelivery
parameter_list|(
name|Receiver
name|receiver
parameter_list|,
name|Delivery
name|delivery
parameter_list|)
block|{      }
specifier|public
name|void
name|inspectDeliveryUpdate
parameter_list|(
name|Sender
name|sender
parameter_list|,
name|Delivery
name|delivery
parameter_list|)
block|{      }
specifier|public
name|boolean
name|isValid
parameter_list|()
block|{
return|return
name|errorMessage
operator|.
name|get
argument_list|()
operator|!=
literal|null
return|;
block|}
specifier|public
specifier|final
name|void
name|clearErrorMessage
parameter_list|()
block|{
name|errorMessage
operator|.
name|set
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
specifier|public
specifier|final
name|String
name|getErrorMessage
parameter_list|()
block|{
return|return
name|errorMessage
operator|.
name|get
argument_list|()
return|;
block|}
specifier|protected
specifier|final
name|boolean
name|markAsInvalid
parameter_list|(
name|String
name|message
parameter_list|)
block|{
if|if
condition|(
name|message
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|NullPointerException
argument_list|(
literal|"Provided error message cannot be null!"
argument_list|)
throw|;
block|}
return|return
name|errorMessage
operator|.
name|compareAndSet
argument_list|(
literal|null
argument_list|,
name|message
argument_list|)
return|;
block|}
specifier|public
specifier|final
name|void
name|assertValid
parameter_list|()
block|{
specifier|final
name|String
name|assertionErrorMessage
init|=
name|errorMessage
operator|.
name|get
argument_list|()
decl_stmt|;
if|if
condition|(
name|assertionErrorMessage
operator|!=
literal|null
condition|)
block|{
throw|throw
operator|new
name|AssertionError
argument_list|(
name|assertionErrorMessage
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

