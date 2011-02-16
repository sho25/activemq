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
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|command
operator|.
name|Command
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
name|Endpoint
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
name|TransportFilter
import|;
end_import

begin_comment
comment|/**  *   */
end_comment

begin_class
specifier|public
class|class
name|ResponseRedirectInterceptor
extends|extends
name|TransportFilter
block|{
specifier|private
specifier|final
name|UdpTransport
name|transport
decl_stmt|;
specifier|public
name|ResponseRedirectInterceptor
parameter_list|(
name|Transport
name|next
parameter_list|,
name|UdpTransport
name|transport
parameter_list|)
block|{
name|super
argument_list|(
name|next
argument_list|)
expr_stmt|;
name|this
operator|.
name|transport
operator|=
name|transport
expr_stmt|;
block|}
specifier|public
name|void
name|onCommand
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
specifier|final
name|Command
name|command
init|=
operator|(
name|Command
operator|)
name|o
decl_stmt|;
comment|// redirect to the endpoint that the last response came from
name|Endpoint
name|from
init|=
name|command
operator|.
name|getFrom
argument_list|()
decl_stmt|;
name|transport
operator|.
name|setTargetEndpoint
argument_list|(
name|from
argument_list|)
expr_stmt|;
name|super
operator|.
name|onCommand
argument_list|(
name|command
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

