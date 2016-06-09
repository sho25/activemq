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
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|DataInput
import|;
end_import

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
name|DataOutput
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
name|io
operator|.
name|OutputStream
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
name|nio
operator|.
name|channels
operator|.
name|Channels
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|channels
operator|.
name|WritableByteChannel
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
name|transport
operator|.
name|amqp
operator|.
name|message
operator|.
name|InboundTransformer
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
name|wireformat
operator|.
name|WireFormat
import|;
end_import

begin_import
import|import
name|org
operator|.
name|fusesource
operator|.
name|hawtbuf
operator|.
name|Buffer
import|;
end_import

begin_class
specifier|public
class|class
name|AmqpWireFormat
implements|implements
name|WireFormat
block|{
specifier|public
specifier|static
specifier|final
name|long
name|DEFAULT_MAX_FRAME_SIZE
init|=
name|Long
operator|.
name|MAX_VALUE
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|int
name|NO_AMQP_MAX_FRAME_SIZE
init|=
operator|-
literal|1
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|int
name|DEFAULT_CONNECTION_TIMEOUT
init|=
literal|30000
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|int
name|DEFAULT_IDLE_TIMEOUT
init|=
literal|30000
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|int
name|DEFAULT_PRODUCER_CREDIT
init|=
literal|1000
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|boolean
name|DEFAULT_ALLOW_NON_SASL_CONNECTIONS
init|=
literal|true
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|int
name|SASL_PROTOCOL
init|=
literal|3
decl_stmt|;
specifier|private
name|int
name|version
init|=
literal|1
decl_stmt|;
specifier|private
name|long
name|maxFrameSize
init|=
name|DEFAULT_MAX_FRAME_SIZE
decl_stmt|;
specifier|private
name|int
name|maxAmqpFrameSize
init|=
name|NO_AMQP_MAX_FRAME_SIZE
decl_stmt|;
specifier|private
name|int
name|connectAttemptTimeout
init|=
name|DEFAULT_CONNECTION_TIMEOUT
decl_stmt|;
specifier|private
name|int
name|idelTimeout
init|=
name|DEFAULT_IDLE_TIMEOUT
decl_stmt|;
specifier|private
name|int
name|producerCredit
init|=
name|DEFAULT_PRODUCER_CREDIT
decl_stmt|;
specifier|private
name|String
name|transformer
init|=
name|InboundTransformer
operator|.
name|TRANSFORMER_JMS
decl_stmt|;
specifier|private
name|boolean
name|allowNonSaslConnections
init|=
name|DEFAULT_ALLOW_NON_SASL_CONNECTIONS
decl_stmt|;
specifier|private
name|boolean
name|magicRead
init|=
literal|false
decl_stmt|;
specifier|private
name|ResetListener
name|resetListener
decl_stmt|;
specifier|public
interface|interface
name|ResetListener
block|{
name|void
name|onProtocolReset
parameter_list|()
function_decl|;
block|}
annotation|@
name|Override
specifier|public
name|ByteSequence
name|marshal
parameter_list|(
name|Object
name|command
parameter_list|)
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
name|dos
init|=
operator|new
name|DataOutputStream
argument_list|(
name|baos
argument_list|)
decl_stmt|;
name|marshal
argument_list|(
name|command
argument_list|,
name|dos
argument_list|)
expr_stmt|;
name|dos
operator|.
name|close
argument_list|()
expr_stmt|;
return|return
name|baos
operator|.
name|toByteSequence
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|Object
name|unmarshal
parameter_list|(
name|ByteSequence
name|packet
parameter_list|)
throws|throws
name|IOException
block|{
name|ByteArrayInputStream
name|stream
init|=
operator|new
name|ByteArrayInputStream
argument_list|(
name|packet
argument_list|)
decl_stmt|;
name|DataInputStream
name|dis
init|=
operator|new
name|DataInputStream
argument_list|(
name|stream
argument_list|)
decl_stmt|;
return|return
name|unmarshal
argument_list|(
name|dis
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|marshal
parameter_list|(
name|Object
name|command
parameter_list|,
name|DataOutput
name|dataOut
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|command
operator|instanceof
name|ByteBuffer
condition|)
block|{
name|ByteBuffer
name|buffer
init|=
operator|(
name|ByteBuffer
operator|)
name|command
decl_stmt|;
if|if
condition|(
name|dataOut
operator|instanceof
name|OutputStream
condition|)
block|{
name|WritableByteChannel
name|channel
init|=
name|Channels
operator|.
name|newChannel
argument_list|(
operator|(
name|OutputStream
operator|)
name|dataOut
argument_list|)
decl_stmt|;
name|channel
operator|.
name|write
argument_list|(
name|buffer
argument_list|)
expr_stmt|;
block|}
else|else
block|{
while|while
condition|(
name|buffer
operator|.
name|hasRemaining
argument_list|()
condition|)
block|{
name|dataOut
operator|.
name|writeByte
argument_list|(
name|buffer
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
else|else
block|{
name|Buffer
name|frame
init|=
operator|(
name|Buffer
operator|)
name|command
decl_stmt|;
name|frame
operator|.
name|writeTo
argument_list|(
name|dataOut
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|Object
name|unmarshal
parameter_list|(
name|DataInput
name|dataIn
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
operator|!
name|magicRead
condition|)
block|{
name|Buffer
name|magic
init|=
operator|new
name|Buffer
argument_list|(
literal|8
argument_list|)
decl_stmt|;
name|magic
operator|.
name|readFrom
argument_list|(
name|dataIn
argument_list|)
expr_stmt|;
name|magicRead
operator|=
literal|true
expr_stmt|;
return|return
operator|new
name|AmqpHeader
argument_list|(
name|magic
argument_list|,
literal|false
argument_list|)
return|;
block|}
else|else
block|{
name|int
name|size
init|=
name|dataIn
operator|.
name|readInt
argument_list|()
decl_stmt|;
if|if
condition|(
name|size
operator|>
name|maxFrameSize
condition|)
block|{
throw|throw
operator|new
name|AmqpProtocolException
argument_list|(
literal|"Frame size exceeded max frame length."
argument_list|)
throw|;
block|}
elseif|else
if|if
condition|(
name|size
operator|<=
literal|0
condition|)
block|{
throw|throw
operator|new
name|AmqpProtocolException
argument_list|(
literal|"Frame size value was invalid: "
operator|+
name|size
argument_list|)
throw|;
block|}
name|Buffer
name|frame
init|=
operator|new
name|Buffer
argument_list|(
name|size
argument_list|)
decl_stmt|;
name|frame
operator|.
name|bigEndianEditor
argument_list|()
operator|.
name|writeInt
argument_list|(
name|size
argument_list|)
expr_stmt|;
name|frame
operator|.
name|readFrom
argument_list|(
name|dataIn
argument_list|)
expr_stmt|;
name|frame
operator|.
name|clear
argument_list|()
expr_stmt|;
return|return
name|frame
return|;
block|}
block|}
comment|/**      * Given an AMQP header validate that the AMQP magic is present and      * if so that the version and protocol values align with what we support.      *      * @param header      *        the header instance received from the client.      *      * @return true if the header is valid against the current WireFormat.      */
specifier|public
name|boolean
name|isHeaderValid
parameter_list|(
name|AmqpHeader
name|header
parameter_list|)
block|{
if|if
condition|(
operator|!
name|header
operator|.
name|hasValidPrefix
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
operator|!
operator|(
name|header
operator|.
name|getProtocolId
argument_list|()
operator|==
literal|0
operator|||
name|header
operator|.
name|getProtocolId
argument_list|()
operator|==
literal|3
operator|)
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
operator|!
name|isAllowNonSaslConnections
argument_list|()
operator|&&
name|header
operator|.
name|getProtocolId
argument_list|()
operator|!=
name|SASL_PROTOCOL
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
name|header
operator|.
name|getMajor
argument_list|()
operator|!=
literal|1
operator|||
name|header
operator|.
name|getMinor
argument_list|()
operator|!=
literal|0
operator|||
name|header
operator|.
name|getRevision
argument_list|()
operator|!=
literal|0
condition|)
block|{
return|return
literal|false
return|;
block|}
return|return
literal|true
return|;
block|}
comment|/**      * Returns an AMQP Header object that represents the minimally protocol      * versions supported by this transport.  A client that attempts to      * connect with an AMQP version that doesn't at least meat this value      * will receive this prior to the connection being closed.      *      * @return the minimal AMQP version needed from the client.      */
specifier|public
name|AmqpHeader
name|getMinimallySupportedHeader
parameter_list|()
block|{
name|AmqpHeader
name|header
init|=
operator|new
name|AmqpHeader
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|isAllowNonSaslConnections
argument_list|()
condition|)
block|{
name|header
operator|.
name|setProtocolId
argument_list|(
literal|3
argument_list|)
expr_stmt|;
block|}
return|return
name|header
return|;
block|}
annotation|@
name|Override
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
annotation|@
name|Override
specifier|public
name|int
name|getVersion
parameter_list|()
block|{
return|return
name|this
operator|.
name|version
return|;
block|}
specifier|public
name|void
name|resetMagicRead
parameter_list|()
block|{
name|this
operator|.
name|magicRead
operator|=
literal|false
expr_stmt|;
if|if
condition|(
name|resetListener
operator|!=
literal|null
condition|)
block|{
name|resetListener
operator|.
name|onProtocolReset
argument_list|()
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|setProtocolResetListener
parameter_list|(
name|ResetListener
name|listener
parameter_list|)
block|{
name|this
operator|.
name|resetListener
operator|=
name|listener
expr_stmt|;
block|}
specifier|public
name|boolean
name|isMagicRead
parameter_list|()
block|{
return|return
name|this
operator|.
name|magicRead
return|;
block|}
specifier|public
name|long
name|getMaxFrameSize
parameter_list|()
block|{
return|return
name|maxFrameSize
return|;
block|}
specifier|public
name|void
name|setMaxFrameSize
parameter_list|(
name|long
name|maxFrameSize
parameter_list|)
block|{
name|this
operator|.
name|maxFrameSize
operator|=
name|maxFrameSize
expr_stmt|;
block|}
specifier|public
name|int
name|getMaxAmqpFrameSize
parameter_list|()
block|{
return|return
name|maxAmqpFrameSize
return|;
block|}
specifier|public
name|void
name|setMaxAmqpFrameSize
parameter_list|(
name|int
name|maxAmqpFrameSize
parameter_list|)
block|{
name|this
operator|.
name|maxAmqpFrameSize
operator|=
name|maxAmqpFrameSize
expr_stmt|;
block|}
specifier|public
name|boolean
name|isAllowNonSaslConnections
parameter_list|()
block|{
return|return
name|allowNonSaslConnections
return|;
block|}
specifier|public
name|void
name|setAllowNonSaslConnections
parameter_list|(
name|boolean
name|allowNonSaslConnections
parameter_list|)
block|{
name|this
operator|.
name|allowNonSaslConnections
operator|=
name|allowNonSaslConnections
expr_stmt|;
block|}
specifier|public
name|int
name|getConnectAttemptTimeout
parameter_list|()
block|{
return|return
name|connectAttemptTimeout
return|;
block|}
specifier|public
name|void
name|setConnectAttemptTimeout
parameter_list|(
name|int
name|connectAttemptTimeout
parameter_list|)
block|{
name|this
operator|.
name|connectAttemptTimeout
operator|=
name|connectAttemptTimeout
expr_stmt|;
block|}
specifier|public
name|void
name|setProducerCredit
parameter_list|(
name|int
name|producerCredit
parameter_list|)
block|{
name|this
operator|.
name|producerCredit
operator|=
name|producerCredit
expr_stmt|;
block|}
specifier|public
name|int
name|getProducerCredit
parameter_list|()
block|{
return|return
name|producerCredit
return|;
block|}
specifier|public
name|String
name|getTransformer
parameter_list|()
block|{
return|return
name|transformer
return|;
block|}
specifier|public
name|void
name|setTransformer
parameter_list|(
name|String
name|transformer
parameter_list|)
block|{
name|this
operator|.
name|transformer
operator|=
name|transformer
expr_stmt|;
block|}
specifier|public
name|int
name|getIdleTimeout
parameter_list|()
block|{
return|return
name|idelTimeout
return|;
block|}
specifier|public
name|void
name|setIdleTimeout
parameter_list|(
name|int
name|idelTimeout
parameter_list|)
block|{
name|this
operator|.
name|idelTimeout
operator|=
name|idelTimeout
expr_stmt|;
block|}
block|}
end_class

end_unit

