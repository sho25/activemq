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
name|command
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|DataInputStream
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
name|util
operator|.
name|Arrays
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
name|HashMap
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
name|activemq
operator|.
name|state
operator|.
name|CommandVisitor
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
name|ByteArrayInputStream
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
name|ByteArrayOutputStream
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
name|activemq
operator|.
name|util
operator|.
name|MarshallingSupport
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
name|wireformat
operator|.
name|WireFormat
import|;
end_import

begin_comment
comment|/**  * @openwire:marshaller code="1"  *   */
end_comment

begin_class
specifier|public
class|class
name|WireFormatInfo
implements|implements
name|Command
implements|,
name|MarshallAware
block|{
specifier|public
specifier|static
specifier|final
name|byte
name|DATA_STRUCTURE_TYPE
init|=
name|CommandTypes
operator|.
name|WIREFORMAT_INFO
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|int
name|MAX_PROPERTY_SIZE
init|=
literal|1024
operator|*
literal|4
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|byte
name|MAGIC
index|[]
init|=
operator|new
name|byte
index|[]
block|{
literal|'A'
block|,
literal|'c'
block|,
literal|'t'
block|,
literal|'i'
block|,
literal|'v'
block|,
literal|'e'
block|,
literal|'M'
block|,
literal|'Q'
block|}
decl_stmt|;
specifier|protected
name|byte
name|magic
index|[]
init|=
name|MAGIC
decl_stmt|;
specifier|protected
name|int
name|version
decl_stmt|;
specifier|protected
name|ByteSequence
name|marshalledProperties
decl_stmt|;
specifier|protected
specifier|transient
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|properties
decl_stmt|;
specifier|private
specifier|transient
name|Endpoint
name|from
decl_stmt|;
specifier|private
specifier|transient
name|Endpoint
name|to
decl_stmt|;
specifier|public
name|byte
name|getDataStructureType
parameter_list|()
block|{
return|return
name|DATA_STRUCTURE_TYPE
return|;
block|}
specifier|public
name|boolean
name|isWireFormatInfo
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
specifier|public
name|boolean
name|isMarshallAware
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
comment|/**      * @openwire:property version=1 size=8 testSize=-1      */
specifier|public
name|byte
index|[]
name|getMagic
parameter_list|()
block|{
return|return
name|magic
return|;
block|}
specifier|public
name|void
name|setMagic
parameter_list|(
name|byte
index|[]
name|magic
parameter_list|)
block|{
name|this
operator|.
name|magic
operator|=
name|magic
expr_stmt|;
block|}
comment|/**      * @openwire:property version=1      */
specifier|public
name|int
name|getVersion
parameter_list|()
block|{
return|return
name|version
return|;
block|}
specifier|public
name|void
name|setVersion
parameter_list|(
name|int
name|version
parameter_list|)
block|{
name|this
operator|.
name|version
operator|=
name|version
expr_stmt|;
block|}
comment|/**      * @openwire:property version=1      */
specifier|public
name|ByteSequence
name|getMarshalledProperties
parameter_list|()
block|{
return|return
name|marshalledProperties
return|;
block|}
specifier|public
name|void
name|setMarshalledProperties
parameter_list|(
name|ByteSequence
name|marshalledProperties
parameter_list|)
block|{
name|this
operator|.
name|marshalledProperties
operator|=
name|marshalledProperties
expr_stmt|;
block|}
comment|/**      * The endpoint within the transport where this message came from.      */
specifier|public
name|Endpoint
name|getFrom
parameter_list|()
block|{
return|return
name|from
return|;
block|}
specifier|public
name|void
name|setFrom
parameter_list|(
name|Endpoint
name|from
parameter_list|)
block|{
name|this
operator|.
name|from
operator|=
name|from
expr_stmt|;
block|}
comment|/**      * The endpoint within the transport where this message is going to - null      * means all endpoints.      */
specifier|public
name|Endpoint
name|getTo
parameter_list|()
block|{
return|return
name|to
return|;
block|}
specifier|public
name|void
name|setTo
parameter_list|(
name|Endpoint
name|to
parameter_list|)
block|{
name|this
operator|.
name|to
operator|=
name|to
expr_stmt|;
block|}
comment|// ////////////////////
comment|//
comment|// Implementation Methods.
comment|//
comment|// ////////////////////
specifier|public
name|Object
name|getProperty
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|properties
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|marshalledProperties
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
name|properties
operator|=
name|unmarsallProperties
argument_list|(
name|marshalledProperties
argument_list|)
expr_stmt|;
block|}
return|return
name|properties
operator|.
name|get
argument_list|(
name|name
argument_list|)
return|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|getProperties
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|properties
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|marshalledProperties
operator|==
literal|null
condition|)
block|{
return|return
name|Collections
operator|.
name|EMPTY_MAP
return|;
block|}
name|properties
operator|=
name|unmarsallProperties
argument_list|(
name|marshalledProperties
argument_list|)
expr_stmt|;
block|}
return|return
name|Collections
operator|.
name|unmodifiableMap
argument_list|(
name|properties
argument_list|)
return|;
block|}
specifier|public
name|void
name|clearProperties
parameter_list|()
block|{
name|marshalledProperties
operator|=
literal|null
expr_stmt|;
name|properties
operator|=
literal|null
expr_stmt|;
block|}
specifier|public
name|void
name|setProperty
parameter_list|(
name|String
name|name
parameter_list|,
name|Object
name|value
parameter_list|)
throws|throws
name|IOException
block|{
name|lazyCreateProperties
argument_list|()
expr_stmt|;
name|properties
operator|.
name|put
argument_list|(
name|name
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|void
name|lazyCreateProperties
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|properties
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|marshalledProperties
operator|==
literal|null
condition|)
block|{
name|properties
operator|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|properties
operator|=
name|unmarsallProperties
argument_list|(
name|marshalledProperties
argument_list|)
expr_stmt|;
name|marshalledProperties
operator|=
literal|null
expr_stmt|;
block|}
block|}
block|}
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|unmarsallProperties
parameter_list|(
name|ByteSequence
name|marshalledProperties
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|MarshallingSupport
operator|.
name|unmarshalPrimitiveMap
argument_list|(
operator|new
name|DataInputStream
argument_list|(
operator|new
name|ByteArrayInputStream
argument_list|(
name|marshalledProperties
argument_list|)
argument_list|)
argument_list|,
name|MAX_PROPERTY_SIZE
argument_list|)
return|;
block|}
specifier|public
name|void
name|beforeMarshall
parameter_list|(
name|WireFormat
name|wireFormat
parameter_list|)
throws|throws
name|IOException
block|{
comment|// Need to marshal the properties.
if|if
condition|(
name|marshalledProperties
operator|==
literal|null
operator|&&
name|properties
operator|!=
literal|null
condition|)
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
name|MarshallingSupport
operator|.
name|marshalPrimitiveMap
argument_list|(
name|properties
argument_list|,
name|os
argument_list|)
expr_stmt|;
name|os
operator|.
name|close
argument_list|()
expr_stmt|;
name|marshalledProperties
operator|=
name|baos
operator|.
name|toByteSequence
argument_list|()
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|afterMarshall
parameter_list|(
name|WireFormat
name|wireFormat
parameter_list|)
throws|throws
name|IOException
block|{     }
specifier|public
name|void
name|beforeUnmarshall
parameter_list|(
name|WireFormat
name|wireFormat
parameter_list|)
throws|throws
name|IOException
block|{     }
specifier|public
name|void
name|afterUnmarshall
parameter_list|(
name|WireFormat
name|wireFormat
parameter_list|)
throws|throws
name|IOException
block|{     }
specifier|public
name|boolean
name|isValid
parameter_list|()
block|{
return|return
name|magic
operator|!=
literal|null
operator|&&
name|Arrays
operator|.
name|equals
argument_list|(
name|magic
argument_list|,
name|MAGIC
argument_list|)
return|;
block|}
specifier|public
name|void
name|setResponseRequired
parameter_list|(
name|boolean
name|responseRequired
parameter_list|)
block|{     }
comment|/**      * @throws IOException      */
specifier|public
name|boolean
name|isCacheEnabled
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|Boolean
operator|.
name|TRUE
operator|==
name|getProperty
argument_list|(
literal|"CacheEnabled"
argument_list|)
return|;
block|}
specifier|public
name|void
name|setCacheEnabled
parameter_list|(
name|boolean
name|cacheEnabled
parameter_list|)
throws|throws
name|IOException
block|{
name|setProperty
argument_list|(
literal|"CacheEnabled"
argument_list|,
name|cacheEnabled
condition|?
name|Boolean
operator|.
name|TRUE
else|:
name|Boolean
operator|.
name|FALSE
argument_list|)
expr_stmt|;
block|}
comment|/**      * @throws IOException      */
specifier|public
name|boolean
name|isStackTraceEnabled
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|Boolean
operator|.
name|TRUE
operator|==
name|getProperty
argument_list|(
literal|"StackTraceEnabled"
argument_list|)
return|;
block|}
specifier|public
name|void
name|setStackTraceEnabled
parameter_list|(
name|boolean
name|stackTraceEnabled
parameter_list|)
throws|throws
name|IOException
block|{
name|setProperty
argument_list|(
literal|"StackTraceEnabled"
argument_list|,
name|stackTraceEnabled
condition|?
name|Boolean
operator|.
name|TRUE
else|:
name|Boolean
operator|.
name|FALSE
argument_list|)
expr_stmt|;
block|}
comment|/**      * @throws IOException      */
specifier|public
name|boolean
name|isTcpNoDelayEnabled
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|Boolean
operator|.
name|TRUE
operator|==
name|getProperty
argument_list|(
literal|"TcpNoDelayEnabled"
argument_list|)
return|;
block|}
specifier|public
name|void
name|setTcpNoDelayEnabled
parameter_list|(
name|boolean
name|tcpNoDelayEnabled
parameter_list|)
throws|throws
name|IOException
block|{
name|setProperty
argument_list|(
literal|"TcpNoDelayEnabled"
argument_list|,
name|tcpNoDelayEnabled
condition|?
name|Boolean
operator|.
name|TRUE
else|:
name|Boolean
operator|.
name|FALSE
argument_list|)
expr_stmt|;
block|}
comment|/**      * @throws IOException      */
specifier|public
name|boolean
name|isSizePrefixDisabled
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|Boolean
operator|.
name|TRUE
operator|==
name|getProperty
argument_list|(
literal|"SizePrefixDisabled"
argument_list|)
return|;
block|}
specifier|public
name|void
name|setSizePrefixDisabled
parameter_list|(
name|boolean
name|prefixPacketSize
parameter_list|)
throws|throws
name|IOException
block|{
name|setProperty
argument_list|(
literal|"SizePrefixDisabled"
argument_list|,
name|prefixPacketSize
condition|?
name|Boolean
operator|.
name|TRUE
else|:
name|Boolean
operator|.
name|FALSE
argument_list|)
expr_stmt|;
block|}
comment|/**      * @throws IOException      */
specifier|public
name|boolean
name|isTightEncodingEnabled
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|Boolean
operator|.
name|TRUE
operator|==
name|getProperty
argument_list|(
literal|"TightEncodingEnabled"
argument_list|)
return|;
block|}
specifier|public
name|void
name|setTightEncodingEnabled
parameter_list|(
name|boolean
name|tightEncodingEnabled
parameter_list|)
throws|throws
name|IOException
block|{
name|setProperty
argument_list|(
literal|"TightEncodingEnabled"
argument_list|,
name|tightEncodingEnabled
condition|?
name|Boolean
operator|.
name|TRUE
else|:
name|Boolean
operator|.
name|FALSE
argument_list|)
expr_stmt|;
block|}
comment|/**      * @throws IOException      */
specifier|public
name|long
name|getMaxInactivityDuration
parameter_list|()
throws|throws
name|IOException
block|{
name|Long
name|l
init|=
operator|(
name|Long
operator|)
name|getProperty
argument_list|(
literal|"MaxInactivityDuration"
argument_list|)
decl_stmt|;
return|return
name|l
operator|==
literal|null
condition|?
literal|0
else|:
name|l
operator|.
name|longValue
argument_list|()
return|;
block|}
specifier|public
name|void
name|setMaxInactivityDuration
parameter_list|(
name|long
name|maxInactivityDuration
parameter_list|)
throws|throws
name|IOException
block|{
name|setProperty
argument_list|(
literal|"MaxInactivityDuration"
argument_list|,
operator|new
name|Long
argument_list|(
name|maxInactivityDuration
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|long
name|getMaxInactivityDurationInitalDelay
parameter_list|()
throws|throws
name|IOException
block|{
name|Long
name|l
init|=
operator|(
name|Long
operator|)
name|getProperty
argument_list|(
literal|"MaxInactivityDurationInitalDelay"
argument_list|)
decl_stmt|;
return|return
name|l
operator|==
literal|null
condition|?
literal|0
else|:
name|l
operator|.
name|longValue
argument_list|()
return|;
block|}
specifier|public
name|void
name|setMaxInactivityDurationInitalDelay
parameter_list|(
name|long
name|maxInactivityDurationInitalDelay
parameter_list|)
throws|throws
name|IOException
block|{
name|setProperty
argument_list|(
literal|"MaxInactivityDurationInitalDelay"
argument_list|,
operator|new
name|Long
argument_list|(
name|maxInactivityDurationInitalDelay
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|long
name|getMaxFrameSize
parameter_list|()
throws|throws
name|IOException
block|{
name|Long
name|l
init|=
operator|(
name|Long
operator|)
name|getProperty
argument_list|(
literal|"MaxFrameSize"
argument_list|)
decl_stmt|;
return|return
name|l
operator|==
literal|null
condition|?
literal|0
else|:
name|l
operator|.
name|longValue
argument_list|()
return|;
block|}
specifier|public
name|void
name|setMaxFrameSize
parameter_list|(
name|long
name|maxFrameSize
parameter_list|)
throws|throws
name|IOException
block|{
name|setProperty
argument_list|(
literal|"MaxFrameSize"
argument_list|,
operator|new
name|Long
argument_list|(
name|maxFrameSize
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**      * @throws IOException      */
specifier|public
name|int
name|getCacheSize
parameter_list|()
throws|throws
name|IOException
block|{
name|Integer
name|i
init|=
operator|(
name|Integer
operator|)
name|getProperty
argument_list|(
literal|"CacheSize"
argument_list|)
decl_stmt|;
return|return
name|i
operator|==
literal|null
condition|?
literal|0
else|:
name|i
operator|.
name|intValue
argument_list|()
return|;
block|}
specifier|public
name|void
name|setCacheSize
parameter_list|(
name|int
name|cacheSize
parameter_list|)
throws|throws
name|IOException
block|{
name|setProperty
argument_list|(
literal|"CacheSize"
argument_list|,
operator|new
name|Integer
argument_list|(
name|cacheSize
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|Response
name|visit
parameter_list|(
name|CommandVisitor
name|visitor
parameter_list|)
throws|throws
name|Exception
block|{
return|return
name|visitor
operator|.
name|processWireFormat
argument_list|(
name|this
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|p
init|=
literal|null
decl_stmt|;
try|try
block|{
name|p
operator|=
name|getProperties
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ignore
parameter_list|)
block|{         }
return|return
literal|"WireFormatInfo { version="
operator|+
name|version
operator|+
literal|", properties="
operator|+
name|p
operator|+
literal|", magic="
operator|+
name|toString
argument_list|(
name|magic
argument_list|)
operator|+
literal|"}"
return|;
block|}
specifier|private
name|String
name|toString
parameter_list|(
name|byte
index|[]
name|data
parameter_list|)
block|{
name|StringBuffer
name|sb
init|=
operator|new
name|StringBuffer
argument_list|()
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|'['
argument_list|)
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
if|if
condition|(
name|i
operator|!=
literal|0
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|','
argument_list|)
expr_stmt|;
block|}
name|sb
operator|.
name|append
argument_list|(
operator|(
name|char
operator|)
name|data
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
name|sb
operator|.
name|append
argument_list|(
literal|']'
argument_list|)
expr_stmt|;
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
comment|// /////////////////////////////////////////////////////////////
comment|//
comment|// This are not implemented.
comment|//
comment|// /////////////////////////////////////////////////////////////
specifier|public
name|void
name|setCommandId
parameter_list|(
name|int
name|value
parameter_list|)
block|{     }
specifier|public
name|int
name|getCommandId
parameter_list|()
block|{
return|return
literal|0
return|;
block|}
specifier|public
name|boolean
name|isResponseRequired
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
specifier|public
name|boolean
name|isResponse
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
specifier|public
name|boolean
name|isBrokerInfo
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
specifier|public
name|boolean
name|isMessageDispatch
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
specifier|public
name|boolean
name|isMessage
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
specifier|public
name|boolean
name|isMessageAck
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
specifier|public
name|boolean
name|isMessageDispatchNotification
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
specifier|public
name|boolean
name|isShutdownInfo
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
specifier|public
name|boolean
name|isConnectionControl
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
specifier|public
name|void
name|setCachedMarshalledForm
parameter_list|(
name|WireFormat
name|wireFormat
parameter_list|,
name|ByteSequence
name|data
parameter_list|)
block|{     }
specifier|public
name|ByteSequence
name|getCachedMarshalledForm
parameter_list|(
name|WireFormat
name|wireFormat
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
block|}
end_class

end_unit
