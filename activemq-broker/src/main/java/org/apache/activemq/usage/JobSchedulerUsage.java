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
name|usage
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
name|scheduler
operator|.
name|JobSchedulerStore
import|;
end_import

begin_comment
comment|/**  * Used to keep track of how much of something is being used so that a  * productive working set usage can be controlled. Main use case is manage  * memory usage.  *  * @org.apache.xbean.XBean  *  */
end_comment

begin_class
specifier|public
class|class
name|JobSchedulerUsage
extends|extends
name|Usage
argument_list|<
name|JobSchedulerUsage
argument_list|>
block|{
specifier|private
name|JobSchedulerStore
name|store
decl_stmt|;
specifier|public
name|JobSchedulerUsage
parameter_list|()
block|{
name|super
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|,
literal|1.0f
argument_list|)
expr_stmt|;
block|}
specifier|public
name|JobSchedulerUsage
parameter_list|(
name|String
name|name
parameter_list|,
name|JobSchedulerStore
name|store
parameter_list|)
block|{
name|super
argument_list|(
literal|null
argument_list|,
name|name
argument_list|,
literal|1.0f
argument_list|)
expr_stmt|;
name|this
operator|.
name|store
operator|=
name|store
expr_stmt|;
block|}
specifier|public
name|JobSchedulerUsage
parameter_list|(
name|JobSchedulerUsage
name|parent
parameter_list|,
name|String
name|name
parameter_list|)
block|{
name|super
argument_list|(
name|parent
argument_list|,
name|name
argument_list|,
literal|1.0f
argument_list|)
expr_stmt|;
name|this
operator|.
name|store
operator|=
name|parent
operator|.
name|store
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|long
name|retrieveUsage
parameter_list|()
block|{
if|if
condition|(
name|store
operator|==
literal|null
condition|)
block|{
return|return
literal|0
return|;
block|}
return|return
name|store
operator|.
name|size
argument_list|()
return|;
block|}
specifier|public
name|JobSchedulerStore
name|getStore
parameter_list|()
block|{
return|return
name|store
return|;
block|}
specifier|public
name|void
name|setStore
parameter_list|(
name|JobSchedulerStore
name|store
parameter_list|)
block|{
name|this
operator|.
name|store
operator|=
name|store
expr_stmt|;
name|onLimitChange
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

