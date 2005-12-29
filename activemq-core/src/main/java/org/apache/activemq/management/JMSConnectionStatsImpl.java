begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  *  * Copyright 2005-2006 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|management
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
name|ActiveMQSession
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
name|util
operator|.
name|IndentPrinter
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

begin_comment
comment|/**  * Statistics for a JMS connection  *  * @version $Revision: 1.2 $  */
end_comment

begin_class
specifier|public
class|class
name|JMSConnectionStatsImpl
extends|extends
name|StatsImpl
block|{
specifier|private
name|List
name|sessions
decl_stmt|;
specifier|private
name|boolean
name|transactional
decl_stmt|;
specifier|public
name|JMSConnectionStatsImpl
parameter_list|(
name|List
name|sessions
parameter_list|,
name|boolean
name|transactional
parameter_list|)
block|{
name|this
operator|.
name|sessions
operator|=
name|sessions
expr_stmt|;
name|this
operator|.
name|transactional
operator|=
name|transactional
expr_stmt|;
block|}
specifier|public
name|JMSSessionStatsImpl
index|[]
name|getSessions
parameter_list|()
block|{
comment|// lets make a snapshot before we process them
name|Object
index|[]
name|sessionArray
init|=
name|sessions
operator|.
name|toArray
argument_list|()
decl_stmt|;
name|int
name|size
init|=
name|sessionArray
operator|.
name|length
decl_stmt|;
name|JMSSessionStatsImpl
index|[]
name|answer
init|=
operator|new
name|JMSSessionStatsImpl
index|[
name|size
index|]
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|size
condition|;
name|i
operator|++
control|)
block|{
name|ActiveMQSession
name|session
init|=
operator|(
name|ActiveMQSession
operator|)
name|sessionArray
index|[
name|i
index|]
decl_stmt|;
name|answer
index|[
name|i
index|]
operator|=
name|session
operator|.
name|getSessionStats
argument_list|()
expr_stmt|;
block|}
return|return
name|answer
return|;
block|}
specifier|public
name|void
name|reset
parameter_list|()
block|{
name|super
operator|.
name|reset
argument_list|()
expr_stmt|;
name|JMSSessionStatsImpl
index|[]
name|stats
init|=
name|getSessions
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|,
name|size
init|=
name|stats
operator|.
name|length
init|;
name|i
operator|<
name|size
condition|;
name|i
operator|++
control|)
block|{
name|stats
index|[
name|i
index|]
operator|.
name|reset
argument_list|()
expr_stmt|;
block|}
block|}
specifier|public
name|boolean
name|isTransactional
parameter_list|()
block|{
return|return
name|transactional
return|;
block|}
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|StringBuffer
name|buffer
init|=
operator|new
name|StringBuffer
argument_list|(
literal|"connection{ "
argument_list|)
decl_stmt|;
name|JMSSessionStatsImpl
index|[]
name|array
init|=
name|getSessions
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|array
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|i
operator|>
literal|0
condition|)
block|{
name|buffer
operator|.
name|append
argument_list|(
literal|", "
argument_list|)
expr_stmt|;
block|}
name|buffer
operator|.
name|append
argument_list|(
name|Integer
operator|.
name|toString
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
literal|" = "
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
name|array
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
name|buffer
operator|.
name|append
argument_list|(
literal|" }"
argument_list|)
expr_stmt|;
return|return
name|buffer
operator|.
name|toString
argument_list|()
return|;
block|}
specifier|public
name|void
name|dump
parameter_list|(
name|IndentPrinter
name|out
parameter_list|)
block|{
name|out
operator|.
name|printIndent
argument_list|()
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"connection {"
argument_list|)
expr_stmt|;
name|out
operator|.
name|incrementIndent
argument_list|()
expr_stmt|;
name|JMSSessionStatsImpl
index|[]
name|array
init|=
name|getSessions
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|array
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|JMSSessionStatsImpl
name|sessionStat
init|=
operator|(
name|JMSSessionStatsImpl
operator|)
name|array
index|[
name|i
index|]
decl_stmt|;
name|out
operator|.
name|printIndent
argument_list|()
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"session {"
argument_list|)
expr_stmt|;
name|out
operator|.
name|incrementIndent
argument_list|()
expr_stmt|;
name|sessionStat
operator|.
name|dump
argument_list|(
name|out
argument_list|)
expr_stmt|;
name|out
operator|.
name|decrementIndent
argument_list|()
expr_stmt|;
name|out
operator|.
name|printIndent
argument_list|()
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"}"
argument_list|)
expr_stmt|;
block|}
name|out
operator|.
name|decrementIndent
argument_list|()
expr_stmt|;
name|out
operator|.
name|printIndent
argument_list|()
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"}"
argument_list|)
expr_stmt|;
name|out
operator|.
name|flush
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

