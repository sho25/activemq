begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
operator|.
name|util
package|;
end_package

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|InvocationHandler
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
name|InvocationTargetException
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
name|nio
operator|.
name|ByteBuffer
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
name|Map
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
name|Link
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
name|Sasl
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
name|Transport
import|;
end_import

begin_comment
comment|/**  * Utility that creates proxy objects for the Proton objects which  * won't allow any mutating operations to be applied so that the test  * code does not interact with the proton engine outside the client  * serialization thread.  */
end_comment

begin_class
specifier|public
specifier|final
class|class
name|UnmodifiableProxy
block|{
specifier|private
specifier|static
name|ArrayList
argument_list|<
name|String
argument_list|>
name|blacklist
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
comment|// These methods are mutating but don't take an arguments so they
comment|// aren't automatically filtered out.  We will have to keep an eye
comment|// on proton API in the future and modify this list as it evolves.
static|static
block|{
name|blacklist
operator|.
name|add
argument_list|(
literal|"close"
argument_list|)
expr_stmt|;
name|blacklist
operator|.
name|add
argument_list|(
literal|"free"
argument_list|)
expr_stmt|;
name|blacklist
operator|.
name|add
argument_list|(
literal|"open"
argument_list|)
expr_stmt|;
name|blacklist
operator|.
name|add
argument_list|(
literal|"sasl"
argument_list|)
expr_stmt|;
name|blacklist
operator|.
name|add
argument_list|(
literal|"session"
argument_list|)
expr_stmt|;
name|blacklist
operator|.
name|add
argument_list|(
literal|"close_head"
argument_list|)
expr_stmt|;
name|blacklist
operator|.
name|add
argument_list|(
literal|"close_tail"
argument_list|)
expr_stmt|;
name|blacklist
operator|.
name|add
argument_list|(
literal|"outputConsumed"
argument_list|)
expr_stmt|;
name|blacklist
operator|.
name|add
argument_list|(
literal|"process"
argument_list|)
expr_stmt|;
name|blacklist
operator|.
name|add
argument_list|(
literal|"processInput"
argument_list|)
expr_stmt|;
name|blacklist
operator|.
name|add
argument_list|(
literal|"unbind"
argument_list|)
expr_stmt|;
name|blacklist
operator|.
name|add
argument_list|(
literal|"settle"
argument_list|)
expr_stmt|;
name|blacklist
operator|.
name|add
argument_list|(
literal|"clear"
argument_list|)
expr_stmt|;
name|blacklist
operator|.
name|add
argument_list|(
literal|"detach"
argument_list|)
expr_stmt|;
name|blacklist
operator|.
name|add
argument_list|(
literal|"abort"
argument_list|)
expr_stmt|;
block|}
specifier|private
name|UnmodifiableProxy
parameter_list|()
block|{}
specifier|public
specifier|static
name|Transport
name|transportProxy
parameter_list|(
specifier|final
name|Transport
name|target
parameter_list|)
block|{
name|Transport
name|wrap
init|=
name|wrap
argument_list|(
name|Transport
operator|.
name|class
argument_list|,
name|target
argument_list|)
decl_stmt|;
return|return
name|wrap
return|;
block|}
specifier|public
specifier|static
name|Sasl
name|saslProxy
parameter_list|(
specifier|final
name|Sasl
name|target
parameter_list|)
block|{
return|return
name|wrap
argument_list|(
name|Sasl
operator|.
name|class
argument_list|,
name|target
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|Connection
name|connectionProxy
parameter_list|(
specifier|final
name|Connection
name|target
parameter_list|)
block|{
return|return
name|wrap
argument_list|(
name|Connection
operator|.
name|class
argument_list|,
name|target
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|Session
name|sessionProxy
parameter_list|(
specifier|final
name|Session
name|target
parameter_list|)
block|{
return|return
name|wrap
argument_list|(
name|Session
operator|.
name|class
argument_list|,
name|target
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|Delivery
name|deliveryProxy
parameter_list|(
specifier|final
name|Delivery
name|target
parameter_list|)
block|{
return|return
name|wrap
argument_list|(
name|Delivery
operator|.
name|class
argument_list|,
name|target
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|Link
name|linkProxy
parameter_list|(
specifier|final
name|Link
name|target
parameter_list|)
block|{
return|return
name|wrap
argument_list|(
name|Link
operator|.
name|class
argument_list|,
name|target
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|Receiver
name|receiverProxy
parameter_list|(
specifier|final
name|Receiver
name|target
parameter_list|)
block|{
return|return
name|wrap
argument_list|(
name|Receiver
operator|.
name|class
argument_list|,
name|target
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|Sender
name|senderProxy
parameter_list|(
specifier|final
name|Sender
name|target
parameter_list|)
block|{
return|return
name|wrap
argument_list|(
name|Sender
operator|.
name|class
argument_list|,
name|target
argument_list|)
return|;
block|}
specifier|private
specifier|static
name|boolean
name|isProtonType
parameter_list|(
name|Class
argument_list|<
name|?
argument_list|>
name|clazz
parameter_list|)
block|{
name|String
name|packageName
init|=
name|clazz
operator|.
name|getPackage
argument_list|()
operator|.
name|getName
argument_list|()
decl_stmt|;
if|if
condition|(
name|packageName
operator|.
name|startsWith
argument_list|(
literal|"org.apache.qpid.proton."
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
return|return
literal|false
return|;
block|}
specifier|private
specifier|static
parameter_list|<
name|T
parameter_list|>
name|T
name|wrap
parameter_list|(
name|Class
argument_list|<
name|T
argument_list|>
name|type
parameter_list|,
specifier|final
name|Object
name|target
parameter_list|)
block|{
return|return
name|type
operator|.
name|cast
argument_list|(
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Proxy
operator|.
name|newProxyInstance
argument_list|(
name|type
operator|.
name|getClassLoader
argument_list|()
argument_list|,
operator|new
name|Class
index|[]
block|{
name|type
block|}
argument_list|,
operator|new
name|InvocationHandler
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Object
name|invoke
parameter_list|(
name|Object
name|o
parameter_list|,
name|Method
name|method
parameter_list|,
name|Object
index|[]
name|objects
parameter_list|)
throws|throws
name|Throwable
block|{
if|if
condition|(
literal|"toString"
operator|.
name|equals
argument_list|(
name|method
operator|.
name|getName
argument_list|()
argument_list|)
operator|&&
name|method
operator|.
name|getParameterTypes
argument_list|()
operator|.
name|length
operator|==
literal|0
condition|)
block|{
return|return
literal|"Unmodifiable proxy -> ("
operator|+
name|method
operator|.
name|invoke
argument_list|(
name|target
argument_list|,
name|objects
argument_list|)
operator|+
literal|")"
return|;
block|}
comment|// Don't let methods that mutate be invoked.
if|if
condition|(
name|method
operator|.
name|getParameterTypes
argument_list|()
operator|.
name|length
operator|>
literal|0
condition|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"Cannot mutate outside the Client work thread"
argument_list|)
throw|;
block|}
if|if
condition|(
name|blacklist
operator|.
name|contains
argument_list|(
name|method
operator|.
name|getName
argument_list|()
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"Cannot mutate outside the Client work thread"
argument_list|)
throw|;
block|}
name|Class
argument_list|<
name|?
argument_list|>
name|returnType
init|=
name|method
operator|.
name|getReturnType
argument_list|()
decl_stmt|;
try|try
block|{
name|Object
name|result
init|=
name|method
operator|.
name|invoke
argument_list|(
name|target
argument_list|,
name|objects
argument_list|)
decl_stmt|;
if|if
condition|(
name|result
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
if|if
condition|(
name|returnType
operator|.
name|isPrimitive
argument_list|()
operator|||
name|returnType
operator|.
name|isArray
argument_list|()
operator|||
name|Object
operator|.
name|class
operator|.
name|equals
argument_list|(
name|returnType
argument_list|)
condition|)
block|{
comment|// Skip any other checks
block|}
elseif|else
if|if
condition|(
name|returnType
operator|.
name|isAssignableFrom
argument_list|(
name|ByteBuffer
operator|.
name|class
argument_list|)
condition|)
block|{
comment|// Buffers are modifiable but we can just return null to indicate
comment|// there's nothing there to access.
name|result
operator|=
literal|null
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|returnType
operator|.
name|isAssignableFrom
argument_list|(
name|Map
operator|.
name|class
argument_list|)
condition|)
block|{
comment|// Prevent return of modifiable maps
name|result
operator|=
name|Collections
operator|.
name|unmodifiableMap
argument_list|(
operator|(
name|Map
argument_list|<
name|?
argument_list|,
name|?
argument_list|>
operator|)
name|result
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|isProtonType
argument_list|(
name|returnType
argument_list|)
operator|&&
name|returnType
operator|.
name|isInterface
argument_list|()
condition|)
block|{
comment|// Can't handle the crazy Source / Target types yet as there's two
comment|// different types for Source and Target the result can't be cast to
comment|// the one people actually want to use.
if|if
condition|(
operator|!
name|returnType
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
literal|"org.apache.qpid.proton.amqp.transport.Source"
argument_list|)
operator|&&
operator|!
name|returnType
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
literal|"org.apache.qpid.proton.amqp.messaging.Source"
argument_list|)
operator|&&
operator|!
name|returnType
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
literal|"org.apache.qpid.proton.amqp.transport.Target"
argument_list|)
operator|&&
operator|!
name|returnType
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
literal|"org.apache.qpid.proton.amqp.messaging.Target"
argument_list|)
condition|)
block|{
name|result
operator|=
name|wrap
argument_list|(
name|returnType
argument_list|,
name|result
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|result
return|;
block|}
catch|catch
parameter_list|(
name|InvocationTargetException
name|e
parameter_list|)
block|{
throw|throw
name|e
operator|.
name|getTargetException
argument_list|()
throw|;
block|}
block|}
block|}
argument_list|)
argument_list|)
return|;
block|}
block|}
end_class

end_unit

