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
name|store
operator|.
name|PersistenceAdapter
import|;
end_import

begin_comment
comment|/**  * Used to keep track of how much of something is being used so that a  * productive working set usage can be controlled. Main use case is manage  * memory usage.  *  * @org.apache.xbean.XBean  *  */
end_comment

begin_class
specifier|public
class|class
name|StoreUsage
extends|extends
name|Usage
argument_list|<
name|StoreUsage
argument_list|>
block|{
specifier|private
name|PersistenceAdapter
name|store
decl_stmt|;
specifier|public
name|StoreUsage
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
name|StoreUsage
parameter_list|(
name|String
name|name
parameter_list|,
name|PersistenceAdapter
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
name|StoreUsage
parameter_list|(
name|StoreUsage
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
return|return
literal|0
return|;
return|return
name|store
operator|.
name|size
argument_list|()
return|;
block|}
specifier|public
name|PersistenceAdapter
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
name|PersistenceAdapter
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
annotation|@
name|Override
specifier|public
name|int
name|getPercentUsage
parameter_list|()
block|{
name|usageLock
operator|.
name|writeLock
argument_list|()
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
name|percentUsage
operator|=
name|caclPercentUsage
argument_list|()
expr_stmt|;
return|return
name|super
operator|.
name|getPercentUsage
argument_list|()
return|;
block|}
finally|finally
block|{
name|usageLock
operator|.
name|writeLock
argument_list|()
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|waitForSpace
parameter_list|(
name|long
name|timeout
parameter_list|,
name|int
name|highWaterMark
parameter_list|)
throws|throws
name|InterruptedException
block|{
if|if
condition|(
name|parent
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|parent
operator|.
name|waitForSpace
argument_list|(
name|timeout
argument_list|,
name|highWaterMark
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
return|return
name|super
operator|.
name|waitForSpace
argument_list|(
name|timeout
argument_list|,
name|highWaterMark
argument_list|)
return|;
block|}
block|}
end_class

end_unit

