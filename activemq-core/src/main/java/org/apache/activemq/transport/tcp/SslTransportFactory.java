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
name|tcp
package|;
end_package

begin_import
import|import
name|javax
operator|.
name|net
operator|.
name|ServerSocketFactory
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

begin_import
import|import
name|javax
operator|.
name|net
operator|.
name|ssl
operator|.
name|SSLServerSocketFactory
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|net
operator|.
name|ssl
operator|.
name|SSLSocketFactory
import|;
end_import

begin_comment
comment|/**  * An implementation of the TCP Transport using SSL  *   * @version $Revision: $  */
end_comment

begin_class
specifier|public
class|class
name|SslTransportFactory
extends|extends
name|TcpTransportFactory
block|{
specifier|public
name|SslTransportFactory
parameter_list|()
block|{     }
specifier|protected
name|ServerSocketFactory
name|createServerSocketFactory
parameter_list|()
block|{
return|return
name|SSLServerSocketFactory
operator|.
name|getDefault
argument_list|()
return|;
block|}
specifier|protected
name|SocketFactory
name|createSocketFactory
parameter_list|()
block|{
return|return
name|SSLSocketFactory
operator|.
name|getDefault
argument_list|()
return|;
block|}
block|}
end_class

end_unit

