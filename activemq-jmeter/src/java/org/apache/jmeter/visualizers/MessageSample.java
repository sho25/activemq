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
name|jmeter
operator|.
name|visualizers
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Serializable
import|;
end_import

begin_class
specifier|public
class|class
name|MessageSample
implements|implements
name|Serializable
implements|,
name|Comparable
block|{
specifier|public
name|long
name|count
decl_stmt|;
specifier|public
name|long
name|processed
decl_stmt|;
specifier|public
name|long
name|data
decl_stmt|;
specifier|public
name|MessageSample
parameter_list|(
name|long
name|num
parameter_list|,
name|long
name|data
parameter_list|,
name|long
name|processed
parameter_list|)
block|{
name|this
operator|.
name|count
operator|=
name|num
expr_stmt|;
name|this
operator|.
name|data
operator|=
name|data
expr_stmt|;
name|this
operator|.
name|processed
operator|=
name|processed
expr_stmt|;
block|}
comment|/**      * @return Returns the count.      */
specifier|public
name|long
name|getCount
parameter_list|()
block|{
return|return
name|count
return|;
block|}
comment|/**      * @param count The count to set.      */
specifier|public
name|void
name|setCount
parameter_list|(
name|long
name|count
parameter_list|)
block|{
name|this
operator|.
name|count
operator|=
name|count
expr_stmt|;
block|}
comment|/**      * @return Returns the data.      */
specifier|public
name|long
name|getData
parameter_list|()
block|{
return|return
name|data
return|;
block|}
comment|/**      * @param data The data to set.      */
specifier|public
name|void
name|setData
parameter_list|(
name|long
name|data
parameter_list|)
block|{
name|this
operator|.
name|data
operator|=
name|data
expr_stmt|;
block|}
comment|/**      * @return Returns the processed.      */
specifier|public
name|long
name|getProcessed
parameter_list|()
block|{
return|return
name|processed
return|;
block|}
comment|/**      * @param processed The processed to set.      */
specifier|public
name|void
name|setProcessed
parameter_list|(
name|long
name|processed
parameter_list|)
block|{
name|this
operator|.
name|processed
operator|=
name|processed
expr_stmt|;
block|}
specifier|public
name|MessageSample
parameter_list|()
block|{     }
comment|/* (non-Javadoc)      * @see java.lang.Comparable#compareTo(java.lang.Object)      */
specifier|public
name|int
name|compareTo
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
name|MessageSample
name|oo
init|=
operator|(
name|MessageSample
operator|)
name|o
decl_stmt|;
return|return
operator|(
operator|(
name|count
operator|-
name|oo
operator|.
name|count
operator|)
operator|<
literal|0
condition|?
operator|-
literal|1
else|:
operator|(
name|count
operator|==
name|oo
operator|.
name|count
condition|?
literal|0
else|:
literal|1
operator|)
operator|)
return|;
block|}
block|}
end_class

end_unit

