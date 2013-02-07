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
name|reliable
package|;
end_package

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
name|activemq
operator|.
name|transport
operator|.
name|CommandJoiner
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
name|Transport
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
name|udp
operator|.
name|ResponseRedirectInterceptor
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
name|udp
operator|.
name|UdpTransport
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
name|udp
operator|.
name|UdpTransportTest
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
comment|/**  *  *  */
end_comment

begin_class
specifier|public
class|class
name|UnreliableUdpTransportTest
extends|extends
name|UdpTransportTest
block|{
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|UnreliableUdpTransportTest
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|protected
name|DropCommandStrategy
name|dropStrategy
init|=
operator|new
name|DropCommandStrategy
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|shouldDropCommand
parameter_list|(
name|int
name|commandId
parameter_list|,
name|SocketAddress
name|address
parameter_list|,
name|boolean
name|redelivery
parameter_list|)
block|{
if|if
condition|(
name|redelivery
condition|)
block|{
return|return
literal|false
return|;
block|}
return|return
name|commandId
operator|%
literal|3
operator|==
literal|2
return|;
block|}
block|}
decl_stmt|;
annotation|@
name|Override
specifier|protected
name|Transport
name|createProducer
parameter_list|()
throws|throws
name|Exception
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Producer using URI: "
operator|+
name|producerURI
argument_list|)
expr_stmt|;
name|OpenWireFormat
name|wireFormat
init|=
name|createWireFormat
argument_list|()
decl_stmt|;
name|UnreliableUdpTransport
name|transport
init|=
operator|new
name|UnreliableUdpTransport
argument_list|(
name|wireFormat
argument_list|,
operator|new
name|URI
argument_list|(
name|producerURI
argument_list|)
argument_list|)
decl_stmt|;
name|transport
operator|.
name|setDropCommandStrategy
argument_list|(
name|dropStrategy
argument_list|)
expr_stmt|;
name|ReliableTransport
name|reliableTransport
init|=
operator|new
name|ReliableTransport
argument_list|(
name|transport
argument_list|,
name|transport
argument_list|)
decl_stmt|;
name|Replayer
name|replayer
init|=
name|reliableTransport
operator|.
name|getReplayer
argument_list|()
decl_stmt|;
name|reliableTransport
operator|.
name|setReplayStrategy
argument_list|(
name|createReplayStrategy
argument_list|(
name|replayer
argument_list|)
argument_list|)
expr_stmt|;
return|return
operator|new
name|CommandJoiner
argument_list|(
name|reliableTransport
argument_list|,
name|wireFormat
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|protected
name|Transport
name|createConsumer
parameter_list|()
throws|throws
name|Exception
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Consumer on port: "
operator|+
name|consumerPort
argument_list|)
expr_stmt|;
name|OpenWireFormat
name|wireFormat
init|=
name|createWireFormat
argument_list|()
decl_stmt|;
name|UdpTransport
name|transport
init|=
operator|new
name|UdpTransport
argument_list|(
name|wireFormat
argument_list|,
name|consumerPort
argument_list|)
decl_stmt|;
name|ReliableTransport
name|reliableTransport
init|=
operator|new
name|ReliableTransport
argument_list|(
name|transport
argument_list|,
name|transport
argument_list|)
decl_stmt|;
name|Replayer
name|replayer
init|=
name|reliableTransport
operator|.
name|getReplayer
argument_list|()
decl_stmt|;
name|reliableTransport
operator|.
name|setReplayStrategy
argument_list|(
name|createReplayStrategy
argument_list|(
name|replayer
argument_list|)
argument_list|)
expr_stmt|;
name|ResponseRedirectInterceptor
name|redirectInterceptor
init|=
operator|new
name|ResponseRedirectInterceptor
argument_list|(
name|reliableTransport
argument_list|,
name|transport
argument_list|)
decl_stmt|;
return|return
operator|new
name|CommandJoiner
argument_list|(
name|redirectInterceptor
argument_list|,
name|wireFormat
argument_list|)
return|;
block|}
specifier|protected
name|ReplayStrategy
name|createReplayStrategy
parameter_list|(
name|Replayer
name|replayer
parameter_list|)
block|{
name|assertNotNull
argument_list|(
literal|"Should have a replayer!"
argument_list|,
name|replayer
argument_list|)
expr_stmt|;
return|return
operator|new
name|DefaultReplayStrategy
argument_list|(
literal|1
argument_list|)
return|;
block|}
block|}
end_class

end_unit

