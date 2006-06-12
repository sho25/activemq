begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  *  * Copyright 2005-2006 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
package|;
end_package

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
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|command
operator|.
name|Command
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
name|WireFormatInfo
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
name|openwire
operator|.
name|OpenWireFormat
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
name|atomic
operator|.
name|AtomicBoolean
import|;
end_import

begin_class
specifier|public
class|class
name|WireFormatNegotiator
extends|extends
name|TransportFilter
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
name|WireFormatNegotiator
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
name|OpenWireFormat
name|wireFormat
decl_stmt|;
specifier|private
specifier|final
name|int
name|minimumVersion
decl_stmt|;
specifier|private
specifier|final
name|AtomicBoolean
name|firstStart
init|=
operator|new
name|AtomicBoolean
argument_list|(
literal|true
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|CountDownLatch
name|readyCountDownLatch
init|=
operator|new
name|CountDownLatch
argument_list|(
literal|1
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|CountDownLatch
name|wireInfoSentDownLatch
init|=
operator|new
name|CountDownLatch
argument_list|(
literal|1
argument_list|)
decl_stmt|;
comment|/**      * Negotiator      *       * @param next      * @param preferedFormat      */
specifier|public
name|WireFormatNegotiator
parameter_list|(
name|Transport
name|next
parameter_list|,
name|OpenWireFormat
name|wireFormat
parameter_list|,
name|int
name|minimumVersion
parameter_list|)
block|{
name|super
argument_list|(
name|next
argument_list|)
expr_stmt|;
name|this
operator|.
name|wireFormat
operator|=
name|wireFormat
expr_stmt|;
name|this
operator|.
name|minimumVersion
operator|=
name|minimumVersion
expr_stmt|;
block|}
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
if|if
condition|(
name|firstStart
operator|.
name|compareAndSet
argument_list|(
literal|true
argument_list|,
literal|false
argument_list|)
condition|)
block|{
try|try
block|{
name|WireFormatInfo
name|info
init|=
name|wireFormat
operator|.
name|getPreferedWireFormatInfo
argument_list|()
decl_stmt|;
if|if
condition|(
name|log
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"Sending: "
operator|+
name|info
argument_list|)
expr_stmt|;
block|}
name|sendWireFormat
argument_list|(
name|info
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|wireInfoSentDownLatch
operator|.
name|countDown
argument_list|()
expr_stmt|;
block|}
block|}
block|}
specifier|public
name|void
name|oneway
parameter_list|(
name|Command
name|command
parameter_list|)
throws|throws
name|IOException
block|{
try|try
block|{
name|readyCountDownLatch
operator|.
name|await
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
name|super
operator|.
name|oneway
argument_list|(
name|command
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|onCommand
parameter_list|(
name|Command
name|command
parameter_list|)
block|{
if|if
condition|(
name|command
operator|.
name|isWireFormatInfo
argument_list|()
condition|)
block|{
name|WireFormatInfo
name|info
init|=
operator|(
name|WireFormatInfo
operator|)
name|command
decl_stmt|;
if|if
condition|(
name|log
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"Received WireFormat: "
operator|+
name|info
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|wireInfoSentDownLatch
operator|.
name|await
argument_list|()
expr_stmt|;
if|if
condition|(
name|log
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|log
operator|.
name|debug
argument_list|(
name|this
operator|+
literal|" before negotiation: "
operator|+
name|wireFormat
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|info
operator|.
name|isValid
argument_list|()
condition|)
block|{
name|onException
argument_list|(
operator|new
name|IOException
argument_list|(
literal|"Remote wire format magic is invalid"
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|info
operator|.
name|getVersion
argument_list|()
operator|<
name|minimumVersion
condition|)
block|{
name|onException
argument_list|(
operator|new
name|IOException
argument_list|(
literal|"Remote wire format ("
operator|+
name|info
operator|.
name|getVersion
argument_list|()
operator|+
literal|") is lower the minimum version required ("
operator|+
name|minimumVersion
operator|+
literal|")"
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|wireFormat
operator|.
name|renegotiateWireFormat
argument_list|(
name|info
argument_list|)
expr_stmt|;
if|if
condition|(
name|log
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|log
operator|.
name|debug
argument_list|(
name|this
operator|+
literal|" after negotiation: "
operator|+
name|wireFormat
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|onException
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
name|onException
argument_list|(
operator|(
name|IOException
operator|)
operator|new
name|InterruptedIOException
argument_list|()
operator|.
name|initCause
argument_list|(
name|e
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|readyCountDownLatch
operator|.
name|countDown
argument_list|()
expr_stmt|;
name|onWireFormatNegotiated
argument_list|(
name|info
argument_list|)
expr_stmt|;
block|}
name|getTransportListener
argument_list|()
operator|.
name|onCommand
argument_list|(
name|command
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|onException
parameter_list|(
name|IOException
name|error
parameter_list|)
block|{
name|readyCountDownLatch
operator|.
name|countDown
argument_list|()
expr_stmt|;
name|super
operator|.
name|onException
argument_list|(
name|error
argument_list|)
expr_stmt|;
block|}
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|next
operator|.
name|toString
argument_list|()
return|;
block|}
specifier|protected
name|void
name|sendWireFormat
parameter_list|(
name|WireFormatInfo
name|info
parameter_list|)
throws|throws
name|IOException
block|{
name|next
operator|.
name|oneway
argument_list|(
name|info
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|void
name|onWireFormatNegotiated
parameter_list|(
name|WireFormatInfo
name|info
parameter_list|)
block|{     }
block|}
end_class

end_unit

