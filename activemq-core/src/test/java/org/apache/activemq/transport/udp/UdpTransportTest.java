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
operator|.
name|udp
package|;
end_package

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
name|TransportFactory
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
specifier|protected
name|int
name|consumerPort
init|=
literal|8830
decl_stmt|;
specifier|protected
name|String
name|producerURI
init|=
literal|"udp://localhost:"
operator|+
name|consumerPort
decl_stmt|;
comment|//protected String producerURI = "udp://localhost:8830";
comment|//protected String consumerURI = "udp://localhost:8831?port=8830";
specifier|protected
name|Transport
name|createProducer
parameter_list|()
throws|throws
name|Exception
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Producer using URI: "
operator|+
name|producerURI
argument_list|)
expr_stmt|;
comment|// The WireFormatNegotiator means we can only connect to servers
return|return
operator|new
name|UdpTransport
argument_list|(
name|createWireFormat
argument_list|()
argument_list|,
operator|new
name|URI
argument_list|(
name|producerURI
argument_list|)
argument_list|)
return|;
comment|//return TransportFactory.connect(new URI(producerURI));
block|}
specifier|protected
name|Transport
name|createConsumer
parameter_list|()
throws|throws
name|Exception
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Consumer on port: "
operator|+
name|consumerPort
argument_list|)
expr_stmt|;
return|return
operator|new
name|UdpTransport
argument_list|(
name|createWireFormat
argument_list|()
argument_list|,
name|consumerPort
argument_list|)
return|;
comment|//return TransportFactory.connect(new URI(consumerURI));
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

