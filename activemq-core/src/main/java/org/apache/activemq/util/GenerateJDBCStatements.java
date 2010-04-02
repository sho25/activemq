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
name|util
package|;
end_package

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Method
import|;
end_import

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
name|activemq
operator|.
name|store
operator|.
name|jdbc
operator|.
name|Statements
import|;
end_import

begin_class
specifier|public
class|class
name|GenerateJDBCStatements
block|{
specifier|public
specifier|static
name|String
name|returnStatement
parameter_list|(
name|Object
name|statement
parameter_list|)
block|{
return|return
operator|(
operator|(
name|String
operator|)
name|statement
operator|)
operator|.
name|replace
argument_list|(
literal|"<"
argument_list|,
literal|"&lt;"
argument_list|)
operator|.
name|replace
argument_list|(
literal|">"
argument_list|,
literal|"&gt;"
argument_list|)
return|;
block|}
comment|/** 	 * @param args 	 */
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
throws|throws
name|Exception
block|{
name|Statements
name|s
init|=
operator|new
name|Statements
argument_list|()
decl_stmt|;
name|s
operator|.
name|setTablePrefix
argument_list|(
literal|"ACTIVEMQ."
argument_list|)
expr_stmt|;
name|String
index|[]
name|stats
init|=
name|s
operator|.
name|getCreateSchemaStatements
argument_list|()
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"<bean id=\"statements\" class=\"org.apache.activemq.store.jdbc.Statements\">"
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"<property name=\"createSchemaStatements\">"
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"<list>"
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
name|stats
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"<value>"
operator|+
name|stats
index|[
name|i
index|]
operator|+
literal|"</value>"
argument_list|)
expr_stmt|;
block|}
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"</list>"
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"</property>"
argument_list|)
expr_stmt|;
name|Method
index|[]
name|methods
init|=
name|Statements
operator|.
name|class
operator|.
name|getMethods
argument_list|()
decl_stmt|;
name|Pattern
name|sPattern
init|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|"get.*Statement$"
argument_list|)
decl_stmt|;
name|Pattern
name|setPattern
init|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|"set.*Statement$"
argument_list|)
decl_stmt|;
name|ArrayList
argument_list|<
name|String
argument_list|>
name|setMethods
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
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
name|methods
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|setPattern
operator|.
name|matcher
argument_list|(
name|methods
index|[
name|i
index|]
operator|.
name|getName
argument_list|()
argument_list|)
operator|.
name|find
argument_list|()
condition|)
block|{
name|setMethods
operator|.
name|add
argument_list|(
name|methods
index|[
name|i
index|]
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|methods
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|sPattern
operator|.
name|matcher
argument_list|(
name|methods
index|[
name|i
index|]
operator|.
name|getName
argument_list|()
argument_list|)
operator|.
name|find
argument_list|()
operator|&&
name|setMethods
operator|.
name|contains
argument_list|(
name|methods
index|[
name|i
index|]
operator|.
name|getName
argument_list|()
operator|.
name|replace
argument_list|(
literal|"get"
argument_list|,
literal|"set"
argument_list|)
argument_list|)
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"<property name=\""
operator|+
name|methods
index|[
name|i
index|]
operator|.
name|getName
argument_list|()
operator|.
name|substring
argument_list|(
literal|3
argument_list|,
literal|4
argument_list|)
operator|.
name|toLowerCase
argument_list|()
operator|+
name|methods
index|[
name|i
index|]
operator|.
name|getName
argument_list|()
operator|.
name|substring
argument_list|(
literal|4
argument_list|)
operator|+
literal|"\" value=\""
operator|+
name|returnStatement
argument_list|(
name|methods
index|[
name|i
index|]
operator|.
name|invoke
argument_list|(
name|s
argument_list|,
literal|null
argument_list|)
argument_list|)
operator|+
literal|"\" />"
argument_list|)
expr_stmt|;
block|}
block|}
comment|//for a typo is not needed if removeMessageStatment typo is corrected
name|Pattern
name|sPattern2
init|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|"get.*Statment$"
argument_list|)
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
name|methods
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|sPattern2
operator|.
name|matcher
argument_list|(
name|methods
index|[
name|i
index|]
operator|.
name|getName
argument_list|()
argument_list|)
operator|.
name|find
argument_list|()
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"<property name=\""
operator|+
name|methods
index|[
name|i
index|]
operator|.
name|getName
argument_list|()
operator|.
name|substring
argument_list|(
literal|3
argument_list|,
literal|4
argument_list|)
operator|.
name|toLowerCase
argument_list|()
operator|+
name|methods
index|[
name|i
index|]
operator|.
name|getName
argument_list|()
operator|.
name|substring
argument_list|(
literal|4
argument_list|)
operator|+
literal|"\" value=\""
operator|+
name|returnStatement
argument_list|(
name|methods
index|[
name|i
index|]
operator|.
name|invoke
argument_list|(
name|s
argument_list|,
literal|null
argument_list|)
argument_list|)
operator|+
literal|"\" />"
argument_list|)
expr_stmt|;
block|}
block|}
comment|//end of generating because of typo
name|String
index|[]
name|statsDrop
init|=
name|s
operator|.
name|getDropSchemaStatements
argument_list|()
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"<property name=\"dropSchemaStatements\">"
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"<list>"
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
name|statsDrop
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"<value>"
operator|+
name|statsDrop
index|[
name|i
index|]
operator|+
literal|"</value>"
argument_list|)
expr_stmt|;
block|}
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"</list>"
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"</property>"
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"</bean>"
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

