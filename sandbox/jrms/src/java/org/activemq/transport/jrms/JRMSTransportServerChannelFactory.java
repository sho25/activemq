begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  *  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|activemq
operator|.
name|transport
operator|.
name|jrms
package|;
end_package

begin_import
import|import
name|org
operator|.
name|activemq
operator|.
name|io
operator|.
name|WireFormat
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
name|TransportServerChannel
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
name|TransportServerChannelFactory
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|JMSException
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
comment|/**  * A multicast implementation of a TransportServerChannelFactory  *   * @version $Revision$  */
end_comment

begin_class
specifier|public
class|class
name|JRMSTransportServerChannelFactory
implements|implements
name|TransportServerChannelFactory
block|{
comment|/**      * Bind a ServerChannel to an address      *      * @param wireFormat      * @param bindAddress      * @return the TransportChannel bound to the remote node      * @throws JMSException      */
specifier|public
name|TransportServerChannel
name|create
parameter_list|(
name|WireFormat
name|wireFormat
parameter_list|,
name|URI
name|bindAddress
parameter_list|)
throws|throws
name|JMSException
block|{
return|return
operator|new
name|JRMSTransportServerChannel
argument_list|(
name|wireFormat
argument_list|,
name|bindAddress
argument_list|)
return|;
block|}
block|}
end_class

end_unit

