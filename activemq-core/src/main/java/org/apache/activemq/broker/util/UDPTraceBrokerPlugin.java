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
name|broker
operator|.
name|util
package|;
end_package

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
name|net
operator|.
name|DatagramPacket
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|DatagramSocket
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|InetAddress
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|InetSocketAddress
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|SocketAddress
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URI
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URISyntaxException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|UnknownHostException
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
name|broker
operator|.
name|BrokerPluginSupport
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
name|broker
operator|.
name|ConnectionContext
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
name|BrokerId
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
name|DataStructure
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
name|Message
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
name|MessageAck
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
name|JournalTrace
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
name|OpenWireFormatFactory
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
name|apache
operator|.
name|activemq
operator|.
name|wireformat
operator|.
name|WireFormatFactory
import|;
end_import

begin_comment
comment|/**  * A Broker interceptor which allows you to trace all operations to a UDP socket.  *   * @org.apache.xbean.XBean  *   * @version $Revision: 427613 $  */
end_comment

begin_class
specifier|public
class|class
name|UDPTraceBrokerPlugin
extends|extends
name|BrokerPluginSupport
block|{
specifier|protected
name|WireFormat
name|wireFormat
decl_stmt|;
specifier|protected
name|WireFormatFactory
name|wireFormatFactory
decl_stmt|;
specifier|protected
name|int
name|maxTraceDatagramSize
init|=
literal|1024
operator|*
literal|4
decl_stmt|;
specifier|protected
name|URI
name|destination
decl_stmt|;
specifier|protected
name|DatagramSocket
name|socket
decl_stmt|;
specifier|protected
name|BrokerId
name|brokerId
decl_stmt|;
specifier|protected
name|SocketAddress
name|address
decl_stmt|;
specifier|protected
name|boolean
name|broadcast
decl_stmt|;
specifier|public
name|UDPTraceBrokerPlugin
parameter_list|()
block|{
try|try
block|{
name|destination
operator|=
operator|new
name|URI
argument_list|(
literal|"udp://127.0.0.1:61616"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|URISyntaxException
name|wontHappen
parameter_list|)
block|{ 		}
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
name|getWireFormat
argument_list|()
operator|==
literal|null
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Wireformat must be specifed."
argument_list|)
throw|;
if|if
condition|(
name|address
operator|==
literal|null
condition|)
block|{
name|address
operator|=
name|createSocketAddress
argument_list|(
name|destination
argument_list|)
expr_stmt|;
block|}
name|socket
operator|=
name|createSocket
argument_list|()
expr_stmt|;
name|brokerId
operator|=
name|super
operator|.
name|getBrokerId
argument_list|()
expr_stmt|;
name|trace
argument_list|(
operator|new
name|JournalTrace
argument_list|(
literal|"START"
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|DatagramSocket
name|createSocket
parameter_list|()
throws|throws
name|IOException
block|{
name|DatagramSocket
name|s
init|=
operator|new
name|DatagramSocket
argument_list|()
decl_stmt|;
name|s
operator|.
name|setSendBufferSize
argument_list|(
name|maxTraceDatagramSize
argument_list|)
expr_stmt|;
name|s
operator|.
name|setBroadcast
argument_list|(
name|broadcast
argument_list|)
expr_stmt|;
return|return
name|s
return|;
block|}
specifier|public
name|void
name|stop
parameter_list|()
throws|throws
name|Exception
block|{
name|trace
argument_list|(
operator|new
name|JournalTrace
argument_list|(
literal|"STOP"
argument_list|)
argument_list|)
expr_stmt|;
name|socket
operator|.
name|close
argument_list|()
expr_stmt|;
name|super
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
specifier|private
name|void
name|trace
parameter_list|(
name|DataStructure
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
argument_list|(
name|maxTraceDatagramSize
argument_list|)
decl_stmt|;
name|DataOutputStream
name|out
init|=
operator|new
name|DataOutputStream
argument_list|(
name|baos
argument_list|)
decl_stmt|;
name|wireFormat
operator|.
name|marshal
argument_list|(
name|brokerId
argument_list|,
name|out
argument_list|)
expr_stmt|;
name|wireFormat
operator|.
name|marshal
argument_list|(
name|command
argument_list|,
name|out
argument_list|)
expr_stmt|;
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
name|ByteSequence
name|sequence
init|=
name|baos
operator|.
name|toByteSequence
argument_list|()
decl_stmt|;
name|DatagramPacket
name|datagram
init|=
operator|new
name|DatagramPacket
argument_list|(
name|sequence
operator|.
name|getData
argument_list|()
argument_list|,
name|sequence
operator|.
name|getOffset
argument_list|()
argument_list|,
name|sequence
operator|.
name|getLength
argument_list|()
argument_list|,
name|address
argument_list|)
decl_stmt|;
name|socket
operator|.
name|send
argument_list|(
name|datagram
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|send
parameter_list|(
name|ConnectionContext
name|context
parameter_list|,
name|Message
name|messageSend
parameter_list|)
throws|throws
name|Exception
block|{
name|trace
argument_list|(
name|messageSend
argument_list|)
expr_stmt|;
name|super
operator|.
name|send
argument_list|(
name|context
argument_list|,
name|messageSend
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|acknowledge
parameter_list|(
name|ConnectionContext
name|context
parameter_list|,
name|MessageAck
name|ack
parameter_list|)
throws|throws
name|Exception
block|{
name|trace
argument_list|(
name|ack
argument_list|)
expr_stmt|;
name|super
operator|.
name|acknowledge
argument_list|(
name|context
argument_list|,
name|ack
argument_list|)
expr_stmt|;
block|}
specifier|public
name|WireFormat
name|getWireFormat
parameter_list|()
block|{
if|if
condition|(
name|wireFormat
operator|==
literal|null
condition|)
block|{
name|wireFormat
operator|=
name|createWireFormat
argument_list|()
expr_stmt|;
block|}
return|return
name|wireFormat
return|;
block|}
specifier|protected
name|WireFormat
name|createWireFormat
parameter_list|()
block|{
return|return
name|getWireFormatFactory
argument_list|()
operator|.
name|createWireFormat
argument_list|()
return|;
block|}
specifier|public
name|void
name|setWireFormat
parameter_list|(
name|WireFormat
name|wireFormat
parameter_list|)
block|{
name|this
operator|.
name|wireFormat
operator|=
name|wireFormat
expr_stmt|;
block|}
specifier|public
name|WireFormatFactory
name|getWireFormatFactory
parameter_list|()
block|{
if|if
condition|(
name|wireFormatFactory
operator|==
literal|null
condition|)
block|{
name|wireFormatFactory
operator|=
name|createWireFormatFactory
argument_list|()
expr_stmt|;
block|}
return|return
name|wireFormatFactory
return|;
block|}
specifier|protected
name|OpenWireFormatFactory
name|createWireFormatFactory
parameter_list|()
block|{
name|OpenWireFormatFactory
name|wf
init|=
operator|new
name|OpenWireFormatFactory
argument_list|()
decl_stmt|;
name|wf
operator|.
name|setCacheEnabled
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|wf
operator|.
name|setVersion
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|wf
operator|.
name|setTightEncodingEnabled
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|wf
operator|.
name|setSizePrefixDisabled
argument_list|(
literal|true
argument_list|)
expr_stmt|;
return|return
name|wf
return|;
block|}
specifier|public
name|void
name|setWireFormatFactory
parameter_list|(
name|WireFormatFactory
name|wireFormatFactory
parameter_list|)
block|{
name|this
operator|.
name|wireFormatFactory
operator|=
name|wireFormatFactory
expr_stmt|;
block|}
specifier|protected
name|SocketAddress
name|createSocketAddress
parameter_list|(
name|URI
name|location
parameter_list|)
throws|throws
name|UnknownHostException
block|{
name|InetAddress
name|a
init|=
name|InetAddress
operator|.
name|getByName
argument_list|(
name|location
operator|.
name|getHost
argument_list|()
argument_list|)
decl_stmt|;
name|int
name|port
init|=
name|location
operator|.
name|getPort
argument_list|()
decl_stmt|;
return|return
operator|new
name|InetSocketAddress
argument_list|(
name|a
argument_list|,
name|port
argument_list|)
return|;
block|}
specifier|public
name|URI
name|getDestination
parameter_list|()
block|{
return|return
name|destination
return|;
block|}
specifier|public
name|void
name|setDestination
parameter_list|(
name|URI
name|destination
parameter_list|)
block|{
name|this
operator|.
name|destination
operator|=
name|destination
expr_stmt|;
block|}
specifier|public
name|int
name|getMaxTraceDatagramSize
parameter_list|()
block|{
return|return
name|maxTraceDatagramSize
return|;
block|}
specifier|public
name|void
name|setMaxTraceDatagramSize
parameter_list|(
name|int
name|maxTraceDatagramSize
parameter_list|)
block|{
name|this
operator|.
name|maxTraceDatagramSize
operator|=
name|maxTraceDatagramSize
expr_stmt|;
block|}
specifier|public
name|boolean
name|isBroadcast
parameter_list|()
block|{
return|return
name|broadcast
return|;
block|}
specifier|public
name|void
name|setBroadcast
parameter_list|(
name|boolean
name|broadcast
parameter_list|)
block|{
name|this
operator|.
name|broadcast
operator|=
name|broadcast
expr_stmt|;
block|}
specifier|public
name|SocketAddress
name|getAddress
parameter_list|()
block|{
return|return
name|address
return|;
block|}
specifier|public
name|void
name|setAddress
parameter_list|(
name|SocketAddress
name|address
parameter_list|)
block|{
name|this
operator|.
name|address
operator|=
name|address
expr_stmt|;
block|}
block|}
end_class

end_unit

