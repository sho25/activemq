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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
import|;
end_import

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
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|JMSException
import|;
end_import

begin_comment
comment|/**  * A MultiExpressionEvaluator is used to evaluate multiple expressions in single  * method call.<p/> Multiple Expression/ExpressionListener pairs can be added  * to a MultiExpressionEvaluator object. When the MultiExpressionEvaluator  * object is evaluated, all the registed Expressions are evaluated and then the  * associated ExpressionListener is invoked to inform it of the evaluation  * result.<p/> By evaluating multiple expressions at one time, some  * optimizations can be made to reduce the number of computations normally  * required to evaluate all the expressions.<p/> When this class adds an  * Expression it wrapps each node in the Expression's AST with a CacheExpression  * object. Then each CacheExpression object (one for each node) is placed in the  * cachedExpressions map. The cachedExpressions map allows us to find the sub  * expressions that are common across two different expressions. When adding an  * Expression in, if a sub Expression of the Expression is allready in the  * cachedExpressions map, then instead of wrapping the sub expression in a new  * CacheExpression object, we reuse the CacheExpression allready int the map.  *<p/> To help illustrate what going on, lets try to give an exmample: If we  * denote the AST of a Expression as follows:  * [AST-Node-Type,Left-Node,Right-Node], then A expression like: "3*5+6" would  * result in "[*,3,[+,5,6]]"<p/> If the [*,3,[+,5,6]] expression is added to  * the MultiExpressionEvaluator, it would really be converted to:  * [c0,[*,3,[c1,[+,5,6]]]] where c0 and c1 represent the CacheExpression  * expression objects that cache the results of the * and the + operation.  * Constants and Property nodes are not cached.<p/> If later on we add the  * following expression [=,11,[+,5,6]] ("11=5+6") to the  * MultiExpressionEvaluator it would be converted to: [c2,[=,11,[c1,[+,5,6]]]],  * where c2 is a new CacheExpression object but c1 is the same CacheExpression  * used in the previous expression.<p/> When the expressions are evaluated, the  * c1 CacheExpression object will only evaluate the [+,5,6] expression once and  * cache the resulting value. Hence evauating the second expression costs less  * because that [+,5,6] is not done 2 times.<p/> Problems: - cacheing the  * values introduces overhead. It may be possible to be smarter about WHICH  * nodes in the AST are cached and which are not. - Current implementation is  * not thread safe. This is because you need a way to invalidate all the cached  * values so that the next evaluation re-evaluates the nodes. By going single  * threaded, chache invalidation is done quickly by incrementing a 'view'  * counter. When a CacheExpressionnotices it's last cached value was generated  * in an old 'view', it invalidates its cached value.  *   *  $Date: 2005/08/27 03:52:36 $  */
end_comment

begin_class
specifier|public
class|class
name|MultiExpressionEvaluator
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|ExpressionListenerSet
argument_list|>
name|rootExpressions
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|ExpressionListenerSet
argument_list|>
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|Expression
argument_list|,
name|CacheExpression
argument_list|>
name|cachedExpressions
init|=
operator|new
name|HashMap
argument_list|<
name|Expression
argument_list|,
name|CacheExpression
argument_list|>
argument_list|()
decl_stmt|;
name|int
name|view
decl_stmt|;
comment|/**      * A UnaryExpression that caches the result of the nested expression. The      * cached value is valid if the      * CacheExpression.cview==MultiExpressionEvaluator.view      */
specifier|public
class|class
name|CacheExpression
extends|extends
name|UnaryExpression
block|{
name|short
name|refCount
decl_stmt|;
name|int
name|cview
init|=
name|view
operator|-
literal|1
decl_stmt|;
name|Object
name|cachedValue
decl_stmt|;
name|int
name|cachedHashCode
decl_stmt|;
specifier|public
name|CacheExpression
parameter_list|(
name|Expression
name|realExpression
parameter_list|)
block|{
name|super
argument_list|(
name|realExpression
argument_list|)
expr_stmt|;
name|cachedHashCode
operator|=
name|realExpression
operator|.
name|hashCode
argument_list|()
expr_stmt|;
block|}
comment|/**          * @see org.apache.activemq.filter.Expression#evaluate(MessageEvaluationContext)          */
specifier|public
name|Object
name|evaluate
parameter_list|(
name|MessageEvaluationContext
name|message
parameter_list|)
throws|throws
name|JMSException
block|{
if|if
condition|(
name|view
operator|==
name|cview
condition|)
block|{
return|return
name|cachedValue
return|;
block|}
name|cachedValue
operator|=
name|right
operator|.
name|evaluate
argument_list|(
name|message
argument_list|)
expr_stmt|;
name|cview
operator|=
name|view
expr_stmt|;
return|return
name|cachedValue
return|;
block|}
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|cachedHashCode
return|;
block|}
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
condition|)
block|{
return|return
literal|false
return|;
block|}
return|return
operator|(
operator|(
name|CacheExpression
operator|)
name|o
operator|)
operator|.
name|right
operator|.
name|equals
argument_list|(
name|right
argument_list|)
return|;
block|}
specifier|public
name|String
name|getExpressionSymbol
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|right
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
comment|/**      * Multiple listeners my be interested in the results of a single      * expression.      */
specifier|static
class|class
name|ExpressionListenerSet
block|{
name|Expression
name|expression
decl_stmt|;
name|List
argument_list|<
name|ExpressionListener
argument_list|>
name|listeners
init|=
operator|new
name|ArrayList
argument_list|<
name|ExpressionListener
argument_list|>
argument_list|()
decl_stmt|;
block|}
comment|/**      * Objects that are interested in the results of an expression should      * implement this interface.      */
specifier|static
interface|interface
name|ExpressionListener
block|{
name|void
name|evaluateResultEvent
parameter_list|(
name|Expression
name|selector
parameter_list|,
name|MessageEvaluationContext
name|message
parameter_list|,
name|Object
name|result
parameter_list|)
function_decl|;
block|}
comment|/**      * Adds an ExpressionListener to a given expression. When evaluate is      * called, the ExpressionListener will be provided the results of the      * Expression applied to the evaluated message.      */
specifier|public
name|void
name|addExpressionListner
parameter_list|(
name|Expression
name|selector
parameter_list|,
name|ExpressionListener
name|c
parameter_list|)
block|{
name|ExpressionListenerSet
name|data
init|=
name|rootExpressions
operator|.
name|get
argument_list|(
name|selector
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|data
operator|==
literal|null
condition|)
block|{
name|data
operator|=
operator|new
name|ExpressionListenerSet
argument_list|()
expr_stmt|;
name|data
operator|.
name|expression
operator|=
name|addToCache
argument_list|(
name|selector
argument_list|)
expr_stmt|;
name|rootExpressions
operator|.
name|put
argument_list|(
name|selector
operator|.
name|toString
argument_list|()
argument_list|,
name|data
argument_list|)
expr_stmt|;
block|}
name|data
operator|.
name|listeners
operator|.
name|add
argument_list|(
name|c
argument_list|)
expr_stmt|;
block|}
comment|/**      * Removes an ExpressionListener from receiving the results of a given      * evaluation.      */
specifier|public
name|boolean
name|removeEventListner
parameter_list|(
name|String
name|selector
parameter_list|,
name|ExpressionListener
name|c
parameter_list|)
block|{
name|String
name|expKey
init|=
name|selector
decl_stmt|;
name|ExpressionListenerSet
name|d
init|=
name|rootExpressions
operator|.
name|get
argument_list|(
name|expKey
argument_list|)
decl_stmt|;
comment|// that selector had not been added.
if|if
condition|(
name|d
operator|==
literal|null
condition|)
block|{
return|return
literal|false
return|;
block|}
comment|// that selector did not have that listeners..
if|if
condition|(
operator|!
name|d
operator|.
name|listeners
operator|.
name|remove
argument_list|(
name|c
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
comment|// If there are no more listeners for this expression....
if|if
condition|(
name|d
operator|.
name|listeners
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
block|{
comment|// Un-cache it...
name|removeFromCache
argument_list|(
operator|(
name|CacheExpression
operator|)
name|d
operator|.
name|expression
argument_list|)
expr_stmt|;
name|rootExpressions
operator|.
name|remove
argument_list|(
name|expKey
argument_list|)
expr_stmt|;
block|}
return|return
literal|true
return|;
block|}
comment|/**      * Finds the CacheExpression that has been associated with an expression. If      * it is the first time the Expression is being added to the Cache, a new      * CacheExpression is created and associated with the expression.<p/> This      * method updates the reference counters on the CacheExpression to know when      * it is no longer needed.      */
specifier|private
name|CacheExpression
name|addToCache
parameter_list|(
name|Expression
name|expr
parameter_list|)
block|{
name|CacheExpression
name|n
init|=
name|cachedExpressions
operator|.
name|get
argument_list|(
name|expr
argument_list|)
decl_stmt|;
if|if
condition|(
name|n
operator|==
literal|null
condition|)
block|{
name|n
operator|=
operator|new
name|CacheExpression
argument_list|(
name|expr
argument_list|)
expr_stmt|;
name|cachedExpressions
operator|.
name|put
argument_list|(
name|expr
argument_list|,
name|n
argument_list|)
expr_stmt|;
if|if
condition|(
name|expr
operator|instanceof
name|UnaryExpression
condition|)
block|{
comment|// Cache the sub expressions too
name|UnaryExpression
name|un
init|=
operator|(
name|UnaryExpression
operator|)
name|expr
decl_stmt|;
name|un
operator|.
name|setRight
argument_list|(
name|addToCache
argument_list|(
name|un
operator|.
name|getRight
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|expr
operator|instanceof
name|BinaryExpression
condition|)
block|{
comment|// Cache the sub expressions too.
name|BinaryExpression
name|bn
init|=
operator|(
name|BinaryExpression
operator|)
name|expr
decl_stmt|;
name|bn
operator|.
name|setRight
argument_list|(
name|addToCache
argument_list|(
name|bn
operator|.
name|getRight
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|bn
operator|.
name|setLeft
argument_list|(
name|addToCache
argument_list|(
name|bn
operator|.
name|getLeft
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|n
operator|.
name|refCount
operator|++
expr_stmt|;
return|return
name|n
return|;
block|}
comment|/**      * Removes an expression from the cache. Updates the reference counters on      * the CacheExpression object. When the refernce counter goes to zero, the      * entry int the Expression to CacheExpression map is removed.      *       * @param cn      */
specifier|private
name|void
name|removeFromCache
parameter_list|(
name|CacheExpression
name|cn
parameter_list|)
block|{
name|cn
operator|.
name|refCount
operator|--
expr_stmt|;
name|Expression
name|realExpr
init|=
name|cn
operator|.
name|getRight
argument_list|()
decl_stmt|;
if|if
condition|(
name|cn
operator|.
name|refCount
operator|==
literal|0
condition|)
block|{
name|cachedExpressions
operator|.
name|remove
argument_list|(
name|realExpr
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|realExpr
operator|instanceof
name|UnaryExpression
condition|)
block|{
name|UnaryExpression
name|un
init|=
operator|(
name|UnaryExpression
operator|)
name|realExpr
decl_stmt|;
name|removeFromCache
argument_list|(
operator|(
name|CacheExpression
operator|)
name|un
operator|.
name|getRight
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|realExpr
operator|instanceof
name|BinaryExpression
condition|)
block|{
name|BinaryExpression
name|bn
init|=
operator|(
name|BinaryExpression
operator|)
name|realExpr
decl_stmt|;
name|removeFromCache
argument_list|(
operator|(
name|CacheExpression
operator|)
name|bn
operator|.
name|getRight
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Evaluates the message against all the Expressions added to this object.      * The added ExpressionListeners are notified of the result of the      * evaluation.      *       * @param message      */
specifier|public
name|void
name|evaluate
parameter_list|(
name|MessageEvaluationContext
name|message
parameter_list|)
block|{
name|Collection
argument_list|<
name|ExpressionListenerSet
argument_list|>
name|expressionListeners
init|=
name|rootExpressions
operator|.
name|values
argument_list|()
decl_stmt|;
for|for
control|(
name|Iterator
argument_list|<
name|ExpressionListenerSet
argument_list|>
name|iter
init|=
name|expressionListeners
operator|.
name|iterator
argument_list|()
init|;
name|iter
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|ExpressionListenerSet
name|els
init|=
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
try|try
block|{
name|Object
name|result
init|=
name|els
operator|.
name|expression
operator|.
name|evaluate
argument_list|(
name|message
argument_list|)
decl_stmt|;
for|for
control|(
name|Iterator
argument_list|<
name|ExpressionListener
argument_list|>
name|iterator
init|=
name|els
operator|.
name|listeners
operator|.
name|iterator
argument_list|()
init|;
name|iterator
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|ExpressionListener
name|l
init|=
name|iterator
operator|.
name|next
argument_list|()
decl_stmt|;
name|l
operator|.
name|evaluateResultEvent
argument_list|(
name|els
operator|.
name|expression
argument_list|,
name|message
argument_list|,
name|result
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

