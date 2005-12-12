begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**   *   * Copyright 2004 Protique Ltd  *   * Licensed under the Apache License, Version 2.0 (the "License");   * you may not use this file except in compliance with the License.   * You may obtain a copy of the License at   *   * http://www.apache.org/licenses/LICENSE-2.0  *   * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,   * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.   * See the License for the specific language governing permissions and   * limitations under the License.   *   **/
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
name|transport
operator|.
name|TransportFactory
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
name|TransportServer
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
name|URI
import|;
end_import

begin_comment
comment|/**  * A<a href="http://stomp.codehaus.org/">Stomp</a> transport factory  *   * @version $Revision: 1.1.1.1 $  */
end_comment

begin_class
specifier|public
class|class
name|StompTransportServerChannelFactory
extends|extends
name|TransportFactory
block|{
specifier|public
name|TransportServer
name|doBind
parameter_list|(
name|String
name|brokerId
parameter_list|,
name|URI
name|location
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|StompTransportServerChannel
argument_list|(
name|brokerId
argument_list|,
name|location
argument_list|)
return|;
block|}
block|}
end_class

end_unit

