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
name|region
operator|.
name|policy
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
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
name|ActiveMQDestination
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
name|filter
operator|.
name|DestinationMap
import|;
end_import

begin_comment
comment|/**  * Represents a destination based configuration of policies so that individual  * destinations or wildcard hierarchies of destinations can be configured using  * different policies.  *   * @org.apache.xbean.XBean  *   * @version $Revision: 1.1 $  */
end_comment

begin_class
specifier|public
class|class
name|PolicyMap
extends|extends
name|DestinationMap
block|{
specifier|private
name|PolicyEntry
name|defaultEntry
decl_stmt|;
specifier|public
name|PolicyEntry
name|getEntryFor
parameter_list|(
name|ActiveMQDestination
name|destination
parameter_list|)
block|{
name|PolicyEntry
name|answer
init|=
operator|(
name|PolicyEntry
operator|)
name|chooseValue
argument_list|(
name|destination
argument_list|)
decl_stmt|;
if|if
condition|(
name|answer
operator|==
literal|null
condition|)
block|{
name|answer
operator|=
name|getDefaultEntry
argument_list|()
expr_stmt|;
block|}
return|return
name|answer
return|;
block|}
comment|/**      * Sets the individual entries on the policy map      *       * @org.apache.xbean.ElementType class="org.apache.activemq.broker.region.policy.PolicyEntry"      */
specifier|public
name|void
name|setPolicyEntries
parameter_list|(
name|List
name|entries
parameter_list|)
block|{
name|super
operator|.
name|setEntries
argument_list|(
name|entries
argument_list|)
expr_stmt|;
block|}
specifier|public
name|PolicyEntry
name|getDefaultEntry
parameter_list|()
block|{
return|return
name|defaultEntry
return|;
block|}
specifier|public
name|void
name|setDefaultEntry
parameter_list|(
name|PolicyEntry
name|defaultEntry
parameter_list|)
block|{
name|this
operator|.
name|defaultEntry
operator|=
name|defaultEntry
expr_stmt|;
block|}
specifier|protected
name|Class
name|getEntryClass
parameter_list|()
block|{
return|return
name|PolicyEntry
operator|.
name|class
return|;
block|}
block|}
end_class

end_unit

