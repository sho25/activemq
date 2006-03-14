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
name|command
operator|.
name|ConsumerInfo
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
name|Response
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
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|transport
operator|.
name|TransportServer
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
name|UdpTransportUsingServerTest
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
specifier|protected
name|String
name|serverURI
init|=
name|producerURI
decl_stmt|;
specifier|public
name|void
name|testRequestResponse
parameter_list|()
throws|throws
name|Exception
block|{
name|ConsumerInfo
name|expected
init|=
operator|new
name|ConsumerInfo
argument_list|()
decl_stmt|;
name|expected
operator|.
name|setSelector
argument_list|(
literal|"Edam"
argument_list|)
expr_stmt|;
name|expected
operator|.
name|setResponseRequired
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"About to send: "
operator|+
name|expected
argument_list|)
expr_stmt|;
name|Response
name|response
init|=
name|producer
operator|.
name|request
argument_list|(
name|expected
argument_list|,
literal|2000
argument_list|)
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Received: "
operator|+
name|response
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
literal|"Received a response"
argument_list|,
name|response
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Should not be an exception"
argument_list|,
operator|!
name|response
operator|.
name|isException
argument_list|()
argument_list|)
expr_stmt|;
block|}
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
name|URI
name|uri
init|=
operator|new
name|URI
argument_list|(
name|producerURI
argument_list|)
decl_stmt|;
return|return
name|TransportFactory
operator|.
name|connect
argument_list|(
name|uri
argument_list|)
return|;
block|}
specifier|protected
name|TransportServer
name|createServer
parameter_list|()
throws|throws
name|Exception
block|{
return|return
name|TransportFactory
operator|.
name|bind
argument_list|(
literal|"byBroker"
argument_list|,
operator|new
name|URI
argument_list|(
name|serverURI
argument_list|)
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
return|return
literal|null
return|;
block|}
block|}
end_class

end_unit

