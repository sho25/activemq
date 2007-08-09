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
name|udp
package|;
end_package

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
name|util
operator|.
name|IntSequenceGenerator
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

begin_comment
comment|/**  *   * @version $Revision$  */
end_comment

begin_class
specifier|public
class|class
name|UdpTransportTest
extends|extends
name|UdpTestSupport
block|{
specifier|private
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|UdpTransportTest
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|protected
name|int
name|consumerPort
init|=
literal|9123
decl_stmt|;
specifier|protected
name|String
name|producerURI
init|=
literal|"udp://localhost:"
operator|+
name|consumerPort
decl_stmt|;
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
comment|// we are not using the TransportFactory as this assumes that
comment|// UDP transports talk to a server using a WireFormat Negotiation step
comment|// rather than talking directly to each other
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
operator|new
name|URI
argument_list|(
name|producerURI
argument_list|)
argument_list|)
decl_stmt|;
name|transport
operator|.
name|setSequenceGenerator
argument_list|(
operator|new
name|IntSequenceGenerator
argument_list|()
argument_list|)
expr_stmt|;
return|return
operator|new
name|CommandJoiner
argument_list|(
name|transport
argument_list|,
name|wireFormat
argument_list|)
return|;
block|}
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
name|transport
operator|.
name|setSequenceGenerator
argument_list|(
operator|new
name|IntSequenceGenerator
argument_list|()
argument_list|)
expr_stmt|;
return|return
operator|new
name|CommandJoiner
argument_list|(
name|transport
argument_list|,
name|wireFormat
argument_list|)
return|;
block|}
specifier|protected
name|OpenWireFormat
name|createWireFormat
parameter_list|()
block|{
return|return
operator|new
name|OpenWireFormat
argument_list|()
return|;
block|}
block|}
end_class

end_unit

