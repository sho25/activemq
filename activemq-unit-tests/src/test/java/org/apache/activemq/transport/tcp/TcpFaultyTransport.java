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
name|tcp
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
name|URI
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
name|Service
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
name|wireformat
operator|.
name|WireFormat
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|net
operator|.
name|SocketFactory
import|;
end_import

begin_comment
comment|/**  * An implementation of the {@link Transport} interface using raw tcp/ip  *   * @author David Martin Clavo david(dot)martin(dot)clavo(at)gmail.com (logging improvement modifications)  *   */
end_comment

begin_class
specifier|public
class|class
name|TcpFaultyTransport
extends|extends
name|TcpTransport
implements|implements
name|Transport
implements|,
name|Service
implements|,
name|Runnable
block|{
specifier|public
name|TcpFaultyTransport
parameter_list|(
name|WireFormat
name|wireFormat
parameter_list|,
name|SocketFactory
name|socketFactory
parameter_list|,
name|URI
name|remoteLocation
parameter_list|,
name|URI
name|localLocation
parameter_list|)
throws|throws
name|UnknownHostException
throws|,
name|IOException
block|{
name|super
argument_list|(
name|wireFormat
argument_list|,
name|socketFactory
argument_list|,
name|remoteLocation
argument_list|,
name|localLocation
argument_list|)
expr_stmt|;
block|}
comment|/**      * @return pretty print of 'this'      */
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"tcpfaulty://"
operator|+
name|socket
operator|.
name|getInetAddress
argument_list|()
operator|+
literal|":"
operator|+
name|socket
operator|.
name|getPort
argument_list|()
return|;
block|}
block|}
end_class

end_unit
