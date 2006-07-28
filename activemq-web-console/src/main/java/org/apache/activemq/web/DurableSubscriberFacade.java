begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  *  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|web
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
name|broker
operator|.
name|BrokerService
import|;
end_import

begin_comment
comment|/**  *   * @version $Revision$  */
end_comment

begin_class
specifier|public
class|class
name|DurableSubscriberFacade
extends|extends
name|DestinationFacade
block|{
specifier|private
name|String
name|clientId
decl_stmt|;
specifier|private
name|String
name|subscriberName
decl_stmt|;
specifier|public
name|DurableSubscriberFacade
parameter_list|(
name|BrokerService
name|brokerService
parameter_list|)
block|{
name|super
argument_list|(
name|brokerService
argument_list|)
expr_stmt|;
block|}
specifier|public
name|String
name|getClientId
parameter_list|()
block|{
return|return
name|clientId
return|;
block|}
specifier|public
name|void
name|setClientId
parameter_list|(
name|String
name|clientId
parameter_list|)
block|{
name|this
operator|.
name|clientId
operator|=
name|clientId
expr_stmt|;
block|}
specifier|public
name|String
name|getSubscriberName
parameter_list|()
block|{
return|return
name|subscriberName
return|;
block|}
specifier|public
name|void
name|setSubscriberName
parameter_list|(
name|String
name|subscriberName
parameter_list|)
block|{
name|this
operator|.
name|subscriberName
operator|=
name|subscriberName
expr_stmt|;
block|}
block|}
end_class

end_unit

