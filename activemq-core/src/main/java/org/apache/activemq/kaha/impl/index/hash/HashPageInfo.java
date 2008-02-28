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
name|kaha
operator|.
name|impl
operator|.
name|index
operator|.
name|hash
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_comment
comment|/**  * A Page within a HashPageInfo  *   * @version $Revision: 1.1.1.1 $  */
end_comment

begin_class
class|class
name|HashPageInfo
block|{
specifier|private
name|HashIndex
name|hashIndex
decl_stmt|;
specifier|private
name|long
name|id
decl_stmt|;
specifier|private
name|int
name|size
decl_stmt|;
specifier|private
name|HashPage
name|page
decl_stmt|;
specifier|private
name|boolean
name|dirty
decl_stmt|;
name|HashPageInfo
parameter_list|(
name|HashIndex
name|index
parameter_list|)
block|{
name|this
operator|.
name|hashIndex
operator|=
name|index
expr_stmt|;
block|}
comment|/**      * @return the id      */
name|long
name|getId
parameter_list|()
block|{
return|return
name|this
operator|.
name|id
return|;
block|}
comment|/**      * @param id the id to set      */
name|void
name|setId
parameter_list|(
name|long
name|id
parameter_list|)
block|{
name|this
operator|.
name|id
operator|=
name|id
expr_stmt|;
block|}
comment|/**      * @return the size      */
name|int
name|size
parameter_list|()
block|{
return|return
name|this
operator|.
name|size
return|;
block|}
name|boolean
name|isEmpty
parameter_list|()
block|{
return|return
name|size
operator|<=
literal|0
return|;
block|}
comment|/**      * @param size the size to set      */
name|void
name|setSize
parameter_list|(
name|int
name|size
parameter_list|)
block|{
name|this
operator|.
name|size
operator|=
name|size
expr_stmt|;
block|}
name|void
name|addHashEntry
parameter_list|(
name|int
name|index
parameter_list|,
name|HashEntry
name|entry
parameter_list|)
throws|throws
name|IOException
block|{
name|page
operator|.
name|addHashEntry
argument_list|(
name|index
argument_list|,
name|entry
argument_list|)
expr_stmt|;
name|size
operator|++
expr_stmt|;
name|dirty
operator|=
literal|true
expr_stmt|;
block|}
name|HashEntry
name|getHashEntry
parameter_list|(
name|int
name|index
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|page
operator|.
name|getHashEntry
argument_list|(
name|index
argument_list|)
return|;
block|}
name|HashEntry
name|removeHashEntry
parameter_list|(
name|int
name|index
parameter_list|)
throws|throws
name|IOException
block|{
name|HashEntry
name|result
init|=
name|page
operator|.
name|removeHashEntry
argument_list|(
name|index
argument_list|)
decl_stmt|;
if|if
condition|(
name|result
operator|!=
literal|null
condition|)
block|{
name|size
operator|--
expr_stmt|;
name|dirty
operator|=
literal|true
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
name|void
name|dump
parameter_list|()
block|{
name|page
operator|.
name|dump
argument_list|()
expr_stmt|;
block|}
name|void
name|begin
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|page
operator|==
literal|null
condition|)
block|{
name|page
operator|=
name|hashIndex
operator|.
name|lookupPage
argument_list|(
name|id
argument_list|)
expr_stmt|;
block|}
block|}
name|void
name|end
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|page
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|dirty
condition|)
block|{
name|hashIndex
operator|.
name|writeFullPage
argument_list|(
name|page
argument_list|)
expr_stmt|;
block|}
block|}
name|page
operator|=
literal|null
expr_stmt|;
name|dirty
operator|=
literal|false
expr_stmt|;
block|}
name|HashPage
name|getPage
parameter_list|()
block|{
return|return
name|page
return|;
block|}
name|void
name|setPage
parameter_list|(
name|HashPage
name|page
parameter_list|)
block|{
name|this
operator|.
name|page
operator|=
name|page
expr_stmt|;
block|}
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"Page["
operator|+
name|id
operator|+
literal|"] size="
operator|+
name|size
return|;
block|}
block|}
end_class

end_unit

