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
package|;
end_package

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
name|amqp
operator|.
name|Binary
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
name|amqp
operator|.
name|transport
operator|.
name|Attach
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
name|amqp
operator|.
name|transport
operator|.
name|Begin
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
name|amqp
operator|.
name|transport
operator|.
name|Close
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
name|amqp
operator|.
name|transport
operator|.
name|Detach
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
name|amqp
operator|.
name|transport
operator|.
name|Disposition
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
name|amqp
operator|.
name|transport
operator|.
name|End
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
name|amqp
operator|.
name|transport
operator|.
name|Flow
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
name|amqp
operator|.
name|transport
operator|.
name|FrameBody
operator|.
name|FrameBodyHandler
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
name|amqp
operator|.
name|transport
operator|.
name|Open
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
name|amqp
operator|.
name|transport
operator|.
name|Transfer
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
name|impl
operator|.
name|ProtocolTracer
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
name|framing
operator|.
name|TransportFrame
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

begin_comment
comment|/**  * Tracer used to spy on AMQP traffic  */
end_comment

begin_class
specifier|public
class|class
name|AmqpProtocolTracer
implements|implements
name|ProtocolTracer
implements|,
name|FrameBodyHandler
argument_list|<
name|AmqpFrameValidator
argument_list|>
block|{
specifier|private
specifier|static
specifier|final
name|Logger
name|TRACE_FRAMES
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|AmqpProtocolTracer
operator|.
name|class
operator|.
name|getPackage
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|".FRAMES"
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|AmqpConnection
name|connection
decl_stmt|;
specifier|public
name|AmqpProtocolTracer
parameter_list|(
name|AmqpConnection
name|connection
parameter_list|)
block|{
name|this
operator|.
name|connection
operator|=
name|connection
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|receivedFrame
parameter_list|(
name|TransportFrame
name|transportFrame
parameter_list|)
block|{
if|if
condition|(
name|connection
operator|.
name|isTraceFrames
argument_list|()
condition|)
block|{
name|TRACE_FRAMES
operator|.
name|trace
argument_list|(
literal|"{} | RECV: {}"
argument_list|,
name|connection
operator|.
name|getRemoteURI
argument_list|()
argument_list|,
name|transportFrame
operator|.
name|getBody
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|AmqpFrameValidator
name|inspector
init|=
name|connection
operator|.
name|getReceivedFrameInspector
argument_list|()
decl_stmt|;
if|if
condition|(
name|inspector
operator|!=
literal|null
condition|)
block|{
name|transportFrame
operator|.
name|getBody
argument_list|()
operator|.
name|invoke
argument_list|(
name|this
argument_list|,
name|transportFrame
operator|.
name|getPayload
argument_list|()
argument_list|,
name|inspector
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|sentFrame
parameter_list|(
name|TransportFrame
name|transportFrame
parameter_list|)
block|{
if|if
condition|(
name|connection
operator|.
name|isTraceFrames
argument_list|()
condition|)
block|{
name|TRACE_FRAMES
operator|.
name|trace
argument_list|(
literal|"{} | SENT: {}"
argument_list|,
name|connection
operator|.
name|getRemoteURI
argument_list|()
argument_list|,
name|transportFrame
operator|.
name|getBody
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|AmqpFrameValidator
name|inspector
init|=
name|connection
operator|.
name|getSentFrameInspector
argument_list|()
decl_stmt|;
if|if
condition|(
name|inspector
operator|!=
literal|null
condition|)
block|{
name|transportFrame
operator|.
name|getBody
argument_list|()
operator|.
name|invoke
argument_list|(
name|this
argument_list|,
name|transportFrame
operator|.
name|getPayload
argument_list|()
argument_list|,
name|inspector
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|handleOpen
parameter_list|(
name|Open
name|open
parameter_list|,
name|Binary
name|payload
parameter_list|,
name|AmqpFrameValidator
name|context
parameter_list|)
block|{
name|context
operator|.
name|inspectOpen
argument_list|(
name|open
argument_list|,
name|payload
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|handleBegin
parameter_list|(
name|Begin
name|begin
parameter_list|,
name|Binary
name|payload
parameter_list|,
name|AmqpFrameValidator
name|context
parameter_list|)
block|{
name|context
operator|.
name|inspectBegin
argument_list|(
name|begin
argument_list|,
name|payload
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|handleAttach
parameter_list|(
name|Attach
name|attach
parameter_list|,
name|Binary
name|payload
parameter_list|,
name|AmqpFrameValidator
name|context
parameter_list|)
block|{
name|context
operator|.
name|inspectAttach
argument_list|(
name|attach
argument_list|,
name|payload
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|handleFlow
parameter_list|(
name|Flow
name|flow
parameter_list|,
name|Binary
name|payload
parameter_list|,
name|AmqpFrameValidator
name|context
parameter_list|)
block|{
name|context
operator|.
name|inspectFlow
argument_list|(
name|flow
argument_list|,
name|payload
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|handleTransfer
parameter_list|(
name|Transfer
name|transfer
parameter_list|,
name|Binary
name|payload
parameter_list|,
name|AmqpFrameValidator
name|context
parameter_list|)
block|{
name|context
operator|.
name|inspectTransfer
argument_list|(
name|transfer
argument_list|,
name|payload
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|handleDisposition
parameter_list|(
name|Disposition
name|disposition
parameter_list|,
name|Binary
name|payload
parameter_list|,
name|AmqpFrameValidator
name|context
parameter_list|)
block|{
name|context
operator|.
name|inspectDisposition
argument_list|(
name|disposition
argument_list|,
name|payload
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|handleDetach
parameter_list|(
name|Detach
name|detach
parameter_list|,
name|Binary
name|payload
parameter_list|,
name|AmqpFrameValidator
name|context
parameter_list|)
block|{
name|context
operator|.
name|inspectDetach
argument_list|(
name|detach
argument_list|,
name|payload
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|handleEnd
parameter_list|(
name|End
name|end
parameter_list|,
name|Binary
name|payload
parameter_list|,
name|AmqpFrameValidator
name|context
parameter_list|)
block|{
name|context
operator|.
name|inspectEnd
argument_list|(
name|end
argument_list|,
name|payload
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|handleClose
parameter_list|(
name|Close
name|close
parameter_list|,
name|Binary
name|payload
parameter_list|,
name|AmqpFrameValidator
name|context
parameter_list|)
block|{
name|context
operator|.
name|inspectClose
argument_list|(
name|close
argument_list|,
name|payload
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

