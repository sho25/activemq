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
name|command
package|;
end_package

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
name|IntrospectionSupport
import|;
end_import

begin_comment
comment|/**  *   * @openwire:marshaller  * @version $Revision: 1.11 $  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|BaseCommand
implements|implements
name|Command
block|{
specifier|protected
name|int
name|commandId
decl_stmt|;
specifier|protected
name|boolean
name|responseRequired
decl_stmt|;
specifier|private
specifier|transient
name|Endpoint
name|from
decl_stmt|;
specifier|private
specifier|transient
name|Endpoint
name|to
decl_stmt|;
specifier|public
name|void
name|copy
parameter_list|(
name|BaseCommand
name|copy
parameter_list|)
block|{
name|copy
operator|.
name|commandId
operator|=
name|commandId
expr_stmt|;
name|copy
operator|.
name|responseRequired
operator|=
name|responseRequired
expr_stmt|;
block|}
comment|/**      * @openwire:property version=1      */
specifier|public
name|int
name|getCommandId
parameter_list|()
block|{
return|return
name|commandId
return|;
block|}
specifier|public
name|void
name|setCommandId
parameter_list|(
name|int
name|commandId
parameter_list|)
block|{
name|this
operator|.
name|commandId
operator|=
name|commandId
expr_stmt|;
block|}
comment|/**      * @openwire:property version=1      */
specifier|public
name|boolean
name|isResponseRequired
parameter_list|()
block|{
return|return
name|responseRequired
return|;
block|}
specifier|public
name|void
name|setResponseRequired
parameter_list|(
name|boolean
name|responseRequired
parameter_list|)
block|{
name|this
operator|.
name|responseRequired
operator|=
name|responseRequired
expr_stmt|;
block|}
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|toString
argument_list|(
literal|null
argument_list|)
return|;
block|}
specifier|public
name|String
name|toString
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|overrideFields
parameter_list|)
block|{
return|return
name|IntrospectionSupport
operator|.
name|toString
argument_list|(
name|this
argument_list|,
name|BaseCommand
operator|.
name|class
argument_list|,
name|overrideFields
argument_list|)
return|;
block|}
specifier|public
name|boolean
name|isWireFormatInfo
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
specifier|public
name|boolean
name|isBrokerInfo
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
specifier|public
name|boolean
name|isResponse
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
specifier|public
name|boolean
name|isMessageDispatch
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
specifier|public
name|boolean
name|isMessage
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
specifier|public
name|boolean
name|isMarshallAware
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
specifier|public
name|boolean
name|isMessageAck
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
specifier|public
name|boolean
name|isMessageDispatchNotification
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
specifier|public
name|boolean
name|isShutdownInfo
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
comment|/**      * The endpoint within the transport where this message came from.      */
specifier|public
name|Endpoint
name|getFrom
parameter_list|()
block|{
return|return
name|from
return|;
block|}
specifier|public
name|void
name|setFrom
parameter_list|(
name|Endpoint
name|from
parameter_list|)
block|{
name|this
operator|.
name|from
operator|=
name|from
expr_stmt|;
block|}
comment|/**      * The endpoint within the transport where this message is going to - null means all endpoints.      */
specifier|public
name|Endpoint
name|getTo
parameter_list|()
block|{
return|return
name|to
return|;
block|}
specifier|public
name|void
name|setTo
parameter_list|(
name|Endpoint
name|to
parameter_list|)
block|{
name|this
operator|.
name|to
operator|=
name|to
expr_stmt|;
block|}
block|}
end_class

end_unit

