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
name|kaha
operator|.
name|impl
package|;
end_package

begin_comment
comment|/**  * A a wrapper for a data in the store  *   * @version $Revision: 1.2 $  */
end_comment

begin_class
specifier|final
class|class
name|DataItem
implements|implements
name|Item
block|{
specifier|private
name|int
name|file
init|=
operator|(
name|int
operator|)
name|POSITION_NOT_SET
decl_stmt|;
specifier|private
name|long
name|offset
init|=
name|POSITION_NOT_SET
decl_stmt|;
specifier|private
name|int
name|size
decl_stmt|;
name|DataItem
parameter_list|()
block|{}
name|DataItem
parameter_list|(
name|DataItem
name|item
parameter_list|)
block|{
name|this
operator|.
name|file
operator|=
name|item
operator|.
name|file
expr_stmt|;
name|this
operator|.
name|offset
operator|=
name|item
operator|.
name|offset
expr_stmt|;
name|this
operator|.
name|size
operator|=
name|item
operator|.
name|size
expr_stmt|;
block|}
name|boolean
name|isValid
parameter_list|()
block|{
return|return
name|file
operator|!=
name|POSITION_NOT_SET
return|;
block|}
comment|/**      * @return Returns the size.      */
name|int
name|getSize
parameter_list|()
block|{
return|return
name|size
return|;
block|}
comment|/**      * @param size The size to set.      */
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
comment|/**      * @return Returns the offset.      */
name|long
name|getOffset
parameter_list|()
block|{
return|return
name|offset
return|;
block|}
comment|/**      * @param offset The offset to set.      */
name|void
name|setOffset
parameter_list|(
name|long
name|offset
parameter_list|)
block|{
name|this
operator|.
name|offset
operator|=
name|offset
expr_stmt|;
block|}
comment|/**      * @return Returns the file.      */
name|int
name|getFile
parameter_list|()
block|{
return|return
name|file
return|;
block|}
comment|/**      * @param file The file to set.      */
name|void
name|setFile
parameter_list|(
name|int
name|file
parameter_list|)
block|{
name|this
operator|.
name|file
operator|=
name|file
expr_stmt|;
block|}
comment|/**      * @return a pretty print      */
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|String
name|result
init|=
literal|"offset = "
operator|+
name|offset
operator|+
literal|", file = "
operator|+
name|file
operator|+
literal|", size = "
operator|+
name|size
decl_stmt|;
return|return
name|result
return|;
block|}
specifier|public
name|DataItem
name|copy
parameter_list|()
block|{
return|return
operator|new
name|DataItem
argument_list|(
name|this
argument_list|)
return|;
block|}
block|}
end_class

end_unit

