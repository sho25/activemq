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

begin_comment
comment|/**  Identify if a limit has been reached  *   * @org.apache.xbean.XBean  *   * @version $Revision: 1.3 $  */
end_comment

begin_class
specifier|public
class|class
name|DefaultUsageCapacity
implements|implements
name|UsageCapacity
block|{
specifier|private
name|long
name|limit
decl_stmt|;
comment|/**      * @param size      * @return true if the limit is reached      * @see org.apache.activemq.usage.UsageCapacity#isLimit(long)      */
specifier|public
name|boolean
name|isLimit
parameter_list|(
name|long
name|size
parameter_list|)
block|{
return|return
name|size
operator|>=
name|limit
return|;
block|}
comment|/**      * @return the limit      */
specifier|public
specifier|final
name|long
name|getLimit
parameter_list|()
block|{
return|return
name|this
operator|.
name|limit
return|;
block|}
comment|/**      * @param limit the limit to set      */
specifier|public
specifier|final
name|void
name|setLimit
parameter_list|(
name|long
name|limit
parameter_list|)
block|{
name|this
operator|.
name|limit
operator|=
name|limit
expr_stmt|;
block|}
block|}
end_class

end_unit

