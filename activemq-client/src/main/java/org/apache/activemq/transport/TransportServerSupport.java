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
name|java
operator|.
name|util
operator|.
name|Map
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
name|ServiceSupport
import|;
end_import

begin_comment
comment|/**  * A useful base class for implementations of {@link TransportServer}  *   *   */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|TransportServerSupport
extends|extends
name|ServiceSupport
implements|implements
name|TransportServer
block|{
specifier|private
name|URI
name|connectURI
decl_stmt|;
specifier|private
name|URI
name|bindLocation
decl_stmt|;
specifier|private
name|TransportAcceptListener
name|acceptListener
decl_stmt|;
specifier|protected
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|transportOptions
decl_stmt|;
specifier|protected
name|boolean
name|allowLinkStealing
decl_stmt|;
specifier|public
name|TransportServerSupport
parameter_list|()
block|{     }
specifier|public
name|TransportServerSupport
parameter_list|(
name|URI
name|location
parameter_list|)
block|{
name|this
operator|.
name|connectURI
operator|=
name|location
expr_stmt|;
name|this
operator|.
name|bindLocation
operator|=
name|location
expr_stmt|;
block|}
comment|/**      * @return Returns the acceptListener.      */
specifier|public
name|TransportAcceptListener
name|getAcceptListener
parameter_list|()
block|{
return|return
name|acceptListener
return|;
block|}
comment|/**      * Registers an accept listener      *       * @param acceptListener      */
specifier|public
name|void
name|setAcceptListener
parameter_list|(
name|TransportAcceptListener
name|acceptListener
parameter_list|)
block|{
name|this
operator|.
name|acceptListener
operator|=
name|acceptListener
expr_stmt|;
block|}
comment|/**      * @return Returns the location.      */
specifier|public
name|URI
name|getConnectURI
parameter_list|()
block|{
return|return
name|connectURI
return|;
block|}
comment|/**      * @param location The location to set.      */
specifier|public
name|void
name|setConnectURI
parameter_list|(
name|URI
name|location
parameter_list|)
block|{
name|this
operator|.
name|connectURI
operator|=
name|location
expr_stmt|;
block|}
specifier|protected
name|void
name|onAcceptError
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
if|if
condition|(
name|acceptListener
operator|!=
literal|null
condition|)
block|{
name|acceptListener
operator|.
name|onAcceptError
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|URI
name|getBindLocation
parameter_list|()
block|{
return|return
name|bindLocation
return|;
block|}
specifier|public
name|void
name|setBindLocation
parameter_list|(
name|URI
name|bindLocation
parameter_list|)
block|{
name|this
operator|.
name|bindLocation
operator|=
name|bindLocation
expr_stmt|;
block|}
specifier|public
name|void
name|setTransportOption
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|transportOptions
parameter_list|)
block|{
name|this
operator|.
name|transportOptions
operator|=
name|transportOptions
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isAllowLinkStealing
parameter_list|()
block|{
return|return
name|allowLinkStealing
return|;
block|}
specifier|public
name|void
name|setAllowLinkStealing
parameter_list|(
name|boolean
name|allowLinkStealing
parameter_list|)
block|{
name|this
operator|.
name|allowLinkStealing
operator|=
name|allowLinkStealing
expr_stmt|;
block|}
block|}
end_class

end_unit

