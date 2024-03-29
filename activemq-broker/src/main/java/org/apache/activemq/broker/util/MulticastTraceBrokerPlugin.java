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
name|IOException
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
name|MulticastSocket
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

begin_comment
comment|/**  * A Broker interceptor which allows you to trace all operations to a Multicast  * socket.  *   * @org.apache.xbean.XBean  *   *   */
end_comment

begin_class
specifier|public
class|class
name|MulticastTraceBrokerPlugin
extends|extends
name|UDPTraceBrokerPlugin
block|{
specifier|private
name|int
name|timeToLive
init|=
literal|1
decl_stmt|;
specifier|public
name|MulticastTraceBrokerPlugin
parameter_list|()
block|{
try|try
block|{
name|destination
operator|=
operator|new
name|URI
argument_list|(
literal|"multicast://224.1.2.3:61616"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|URISyntaxException
name|wontHappen
parameter_list|)
block|{         }
block|}
specifier|protected
name|DatagramSocket
name|createSocket
parameter_list|()
throws|throws
name|IOException
block|{
name|MulticastSocket
name|s
init|=
operator|new
name|MulticastSocket
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
name|s
operator|.
name|setLoopbackMode
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|s
operator|.
name|setTimeToLive
argument_list|(
name|timeToLive
argument_list|)
expr_stmt|;
return|return
name|s
return|;
block|}
specifier|public
name|int
name|getTimeToLive
parameter_list|()
block|{
return|return
name|timeToLive
return|;
block|}
specifier|public
name|void
name|setTimeToLive
parameter_list|(
name|int
name|timeToLive
parameter_list|)
block|{
name|this
operator|.
name|timeToLive
operator|=
name|timeToLive
expr_stmt|;
block|}
block|}
end_class

end_unit

