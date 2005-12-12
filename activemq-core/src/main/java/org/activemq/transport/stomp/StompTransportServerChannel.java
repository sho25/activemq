begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  *   * Copyright 2004 Protique Ltd  *   * Licensed under the Apache License, Version 2.0 (the "License");   * you may not use this file except in compliance with the License.   * You may obtain a copy of the License at   *   * http://www.apache.org/licenses/LICENSE-2.0  *   * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,   * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.   * See the License for the specific language governing permissions and   * limitations under the License.   *   **/
end_comment

begin_package
package|package
name|org
operator|.
name|activemq
operator|.
name|transport
operator|.
name|stomp
package|;
end_package

begin_import
import|import
name|org
operator|.
name|activemq
operator|.
name|command
operator|.
name|BrokerInfo
import|;
end_import

begin_import
import|import
name|org
operator|.
name|activemq
operator|.
name|transport
operator|.
name|TransportServerSupport
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
comment|/**  * A<a href="http://stomp.codehaus.org/">Stomp</a> transport server  *   * @version $Revision: 1.1.1.1 $  */
end_comment

begin_class
specifier|public
class|class
name|StompTransportServerChannel
extends|extends
name|TransportServerSupport
implements|implements
name|Runnable
block|{
specifier|public
name|StompTransportServerChannel
parameter_list|(
name|String
name|brokerId
parameter_list|,
name|URI
name|location
parameter_list|)
block|{
comment|// TODO...
block|}
specifier|public
name|void
name|run
parameter_list|()
block|{     }
specifier|public
name|void
name|setBrokerInfo
parameter_list|(
name|BrokerInfo
name|brokerInfo
parameter_list|)
block|{     }
specifier|public
name|void
name|start
parameter_list|()
throws|throws
name|Exception
block|{     }
specifier|public
name|void
name|stop
parameter_list|()
throws|throws
name|Exception
block|{     }
comment|/*      * protected TcpTransportChannel createTransportChannel(Socket socket,      * PooledExecutor executor) throws JMSException { return new      * StompTransportChannel(socket, executor); }      */
block|}
end_class

end_unit

