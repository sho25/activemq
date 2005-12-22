begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  *  * Copyright 2004 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|activemq
operator|.
name|perf
package|;
end_package

begin_comment
comment|/**  * @version $Revision: 1.3 $  */
end_comment

begin_class
specifier|public
class|class
name|PerfRate
block|{
specifier|protected
name|int
name|totalCount
decl_stmt|;
specifier|protected
name|int
name|count
decl_stmt|;
specifier|protected
name|long
name|startTime
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
comment|/**      * @return Returns the count.      */
specifier|public
name|int
name|getCount
parameter_list|()
block|{
return|return
name|totalCount
return|;
block|}
specifier|public
name|void
name|increment
parameter_list|()
block|{
name|totalCount
operator|++
expr_stmt|;
name|count
operator|++
expr_stmt|;
block|}
specifier|public
name|void
name|start
parameter_list|()
block|{
name|count
operator|=
literal|0
expr_stmt|;
name|startTime
operator|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
expr_stmt|;
block|}
specifier|public
name|int
name|getRate
parameter_list|()
block|{
name|long
name|endTime
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|long
name|totalTime
init|=
name|endTime
operator|-
name|startTime
decl_stmt|;
name|int
name|result
init|=
call|(
name|int
call|)
argument_list|(
operator|(
name|count
operator|*
literal|1000
operator|)
operator|/
name|totalTime
argument_list|)
decl_stmt|;
return|return
name|result
return|;
block|}
comment|/**      * @return Returns the totalCount.      */
specifier|public
name|int
name|getTotalCount
parameter_list|()
block|{
return|return
name|totalCount
return|;
block|}
comment|/**      * @param totalCount      *            The totalCount to set.      */
specifier|public
name|void
name|setTotalCount
parameter_list|(
name|int
name|totalCount
parameter_list|)
block|{
name|this
operator|.
name|totalCount
operator|=
name|totalCount
expr_stmt|;
block|}
block|}
end_class

end_unit

