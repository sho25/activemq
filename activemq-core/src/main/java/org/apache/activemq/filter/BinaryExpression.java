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
name|filter
package|;
end_package

begin_comment
comment|/**  * An expression which performs an operation on two expression values.  *   * @version $Revision: 1.2 $  */
end_comment

begin_class
specifier|abstract
specifier|public
class|class
name|BinaryExpression
implements|implements
name|Expression
block|{
specifier|protected
name|Expression
name|left
decl_stmt|;
specifier|protected
name|Expression
name|right
decl_stmt|;
specifier|public
name|BinaryExpression
parameter_list|(
name|Expression
name|left
parameter_list|,
name|Expression
name|right
parameter_list|)
block|{
name|this
operator|.
name|left
operator|=
name|left
expr_stmt|;
name|this
operator|.
name|right
operator|=
name|right
expr_stmt|;
block|}
specifier|public
name|Expression
name|getLeft
parameter_list|()
block|{
return|return
name|left
return|;
block|}
specifier|public
name|Expression
name|getRight
parameter_list|()
block|{
return|return
name|right
return|;
block|}
comment|/**      * @see java.lang.Object#toString()      */
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"("
operator|+
name|left
operator|.
name|toString
argument_list|()
operator|+
literal|" "
operator|+
name|getExpressionSymbol
argument_list|()
operator|+
literal|" "
operator|+
name|right
operator|.
name|toString
argument_list|()
operator|+
literal|")"
return|;
block|}
comment|/**      * TODO: more efficient hashCode()      *      * @see java.lang.Object#hashCode()      */
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|toString
argument_list|()
operator|.
name|hashCode
argument_list|()
return|;
block|}
comment|/**      * TODO: more efficient hashCode()      *      * @see java.lang.Object#equals(java.lang.Object)      */
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
if|if
condition|(
name|o
operator|==
literal|null
operator|||
operator|!
name|this
operator|.
name|getClass
argument_list|()
operator|.
name|equals
argument_list|(
name|o
operator|.
name|getClass
argument_list|()
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
return|return
name|toString
argument_list|()
operator|.
name|equals
argument_list|(
name|o
operator|.
name|toString
argument_list|()
argument_list|)
return|;
block|}
comment|/**      * Returns the symbol that represents this binary expression.  For example, addition is      * represented by "+"      *      * @return      */
specifier|abstract
specifier|public
name|String
name|getExpressionSymbol
parameter_list|()
function_decl|;
comment|/**      * @param expression      */
specifier|public
name|void
name|setRight
parameter_list|(
name|Expression
name|expression
parameter_list|)
block|{
name|right
operator|=
name|expression
expr_stmt|;
block|}
comment|/**      * @param expression      */
specifier|public
name|void
name|setLeft
parameter_list|(
name|Expression
name|expression
parameter_list|)
block|{
name|left
operator|=
name|expression
expr_stmt|;
block|}
block|}
end_class

end_unit

