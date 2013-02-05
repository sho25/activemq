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
name|bugs
operator|.
name|embedded
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Matcher
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Pattern
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|log4j
operator|.
name|Logger
import|;
end_import

begin_class
specifier|public
class|class
name|ThreadExplorer
block|{
specifier|static
name|Logger
name|logger
init|=
name|Logger
operator|.
name|getLogger
argument_list|(
name|ThreadExplorer
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|public
specifier|static
name|Thread
index|[]
name|listThreads
parameter_list|()
block|{
name|int
name|nThreads
init|=
name|Thread
operator|.
name|activeCount
argument_list|()
decl_stmt|;
name|Thread
name|ret
index|[]
init|=
operator|new
name|Thread
index|[
name|nThreads
index|]
decl_stmt|;
name|Thread
operator|.
name|enumerate
argument_list|(
name|ret
argument_list|)
expr_stmt|;
return|return
name|ret
return|;
block|}
comment|/**      * Helper function to access a thread per name (ignoring case)      *       * @param name      * @return      */
specifier|public
specifier|static
name|Thread
name|fetchThread
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|Thread
index|[]
name|threadArray
init|=
name|listThreads
argument_list|()
decl_stmt|;
comment|// for (Thread t : threadArray)
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|threadArray
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|Thread
name|t
init|=
name|threadArray
index|[
name|i
index|]
decl_stmt|;
if|if
condition|(
name|t
operator|.
name|getName
argument_list|()
operator|.
name|equalsIgnoreCase
argument_list|(
name|name
argument_list|)
condition|)
return|return
name|t
return|;
block|}
return|return
literal|null
return|;
block|}
comment|/**      * Allow for killing threads      *       * @param threadName      * @param isStarredExp      *            (regular expressions with *)      */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"deprecation"
argument_list|)
specifier|public
specifier|static
name|int
name|kill
parameter_list|(
name|String
name|threadName
parameter_list|,
name|boolean
name|isStarredExp
parameter_list|,
name|String
name|motivation
parameter_list|)
block|{
name|String
name|me
init|=
literal|"ThreadExplorer.kill: "
decl_stmt|;
if|if
condition|(
name|logger
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|logger
operator|.
name|debug
argument_list|(
literal|"Entering "
operator|+
name|me
operator|+
literal|" with "
operator|+
name|threadName
operator|+
literal|" isStarred: "
operator|+
name|isStarredExp
argument_list|)
expr_stmt|;
block|}
name|int
name|ret
init|=
literal|0
decl_stmt|;
name|Pattern
name|mypattern
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|isStarredExp
condition|)
block|{
name|String
name|realreg
init|=
name|threadName
operator|.
name|toLowerCase
argument_list|()
operator|.
name|replaceAll
argument_list|(
literal|"\\*"
argument_list|,
literal|"\\.\\*"
argument_list|)
decl_stmt|;
name|mypattern
operator|=
name|Pattern
operator|.
name|compile
argument_list|(
name|realreg
argument_list|)
expr_stmt|;
block|}
name|Thread
index|[]
name|threads
init|=
name|listThreads
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
name|threads
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|Thread
name|thread
init|=
name|threads
index|[
name|i
index|]
decl_stmt|;
if|if
condition|(
name|thread
operator|==
literal|null
condition|)
continue|continue;
comment|// kill the thread unless it is not current thread
name|boolean
name|matches
init|=
literal|false
decl_stmt|;
if|if
condition|(
name|isStarredExp
condition|)
block|{
name|Matcher
name|matcher
init|=
name|mypattern
operator|.
name|matcher
argument_list|(
name|thread
operator|.
name|getName
argument_list|()
operator|.
name|toLowerCase
argument_list|()
argument_list|)
decl_stmt|;
name|matches
operator|=
name|matcher
operator|.
name|matches
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|matches
operator|=
operator|(
name|thread
operator|.
name|getName
argument_list|()
operator|.
name|equalsIgnoreCase
argument_list|(
name|threadName
argument_list|)
operator|)
expr_stmt|;
block|}
if|if
condition|(
name|matches
operator|&&
operator|(
name|Thread
operator|.
name|currentThread
argument_list|()
operator|!=
name|thread
operator|)
operator|&&
operator|!
name|thread
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
literal|"main"
argument_list|)
condition|)
block|{
if|if
condition|(
name|logger
operator|.
name|isInfoEnabled
argument_list|()
condition|)
name|logger
operator|.
name|info
argument_list|(
literal|"Killing thread named ["
operator|+
name|thread
operator|.
name|getName
argument_list|()
operator|+
literal|"]"
argument_list|)
expr_stmt|;
comment|// , removing its uncaught
comment|// exception handler to
comment|// avoid ThreadDeath
comment|// exception tracing
comment|// "+motivation );
name|ret
operator|++
expr_stmt|;
comment|// PK leaving uncaught exception handler otherwise master push
comment|// cannot recover from this error
comment|// thread.setUncaughtExceptionHandler(null);
try|try
block|{
name|thread
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ThreadDeath
name|e
parameter_list|)
block|{
name|logger
operator|.
name|warn
argument_list|(
literal|"Thread already death."
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|return
name|ret
return|;
block|}
specifier|public
specifier|static
name|String
name|show
parameter_list|(
name|String
name|title
parameter_list|)
block|{
name|StringBuffer
name|out
init|=
operator|new
name|StringBuffer
argument_list|()
decl_stmt|;
name|Thread
index|[]
name|threadArray
init|=
name|ThreadExplorer
operator|.
name|listThreads
argument_list|()
decl_stmt|;
name|out
operator|.
name|append
argument_list|(
name|title
operator|+
literal|"\n"
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|threadArray
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|Thread
name|thread
init|=
name|threadArray
index|[
name|i
index|]
decl_stmt|;
if|if
condition|(
name|thread
operator|!=
literal|null
condition|)
block|{
name|out
operator|.
name|append
argument_list|(
literal|"* ["
operator|+
name|thread
operator|.
name|getName
argument_list|()
operator|+
literal|"] "
operator|+
operator|(
name|thread
operator|.
name|isDaemon
argument_list|()
condition|?
literal|"(Daemon)"
else|:
literal|""
operator|)
operator|+
literal|" Group: "
operator|+
name|thread
operator|.
name|getThreadGroup
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|"\n"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|out
operator|.
name|append
argument_list|(
literal|"* ThreadDeath: "
operator|+
name|thread
operator|+
literal|"\n"
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|out
operator|.
name|toString
argument_list|()
return|;
block|}
specifier|public
specifier|static
name|int
name|active
parameter_list|()
block|{
name|int
name|count
init|=
literal|0
decl_stmt|;
name|Thread
index|[]
name|threadArray
init|=
name|ThreadExplorer
operator|.
name|listThreads
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
name|threadArray
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|Thread
name|thread
init|=
name|threadArray
index|[
name|i
index|]
decl_stmt|;
if|if
condition|(
name|thread
operator|!=
literal|null
condition|)
block|{
name|count
operator|++
expr_stmt|;
block|}
block|}
return|return
name|count
return|;
block|}
block|}
end_class

end_unit
