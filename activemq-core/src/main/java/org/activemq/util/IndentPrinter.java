begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** *<a href="http://activemq.org">ActiveMQ: The Open Source Message Fabric</a> * * Copyright 2005 (C) LogicBlaze, Inc. http://www.logicblaze.com * * Licensed under the Apache License, Version 2.0 (the "License"); * you may not use this file except in compliance with the License. * You may obtain a copy of the License at * * http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. * **/
end_comment

begin_package
package|package
name|org
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
name|io
operator|.
name|PrintWriter
import|;
end_import

begin_comment
comment|/**  * A helper class for printing indented text  *  * @version $Revision: 1.2 $  */
end_comment

begin_class
specifier|public
class|class
name|IndentPrinter
block|{
specifier|private
name|int
name|indentLevel
decl_stmt|;
specifier|private
name|String
name|indent
decl_stmt|;
specifier|private
name|PrintWriter
name|out
decl_stmt|;
specifier|public
name|IndentPrinter
parameter_list|()
block|{
name|this
argument_list|(
operator|new
name|PrintWriter
argument_list|(
name|System
operator|.
name|out
argument_list|)
argument_list|,
literal|"  "
argument_list|)
expr_stmt|;
block|}
specifier|public
name|IndentPrinter
parameter_list|(
name|PrintWriter
name|out
parameter_list|)
block|{
name|this
argument_list|(
name|out
argument_list|,
literal|"  "
argument_list|)
expr_stmt|;
block|}
specifier|public
name|IndentPrinter
parameter_list|(
name|PrintWriter
name|out
parameter_list|,
name|String
name|indent
parameter_list|)
block|{
name|this
operator|.
name|out
operator|=
name|out
expr_stmt|;
name|this
operator|.
name|indent
operator|=
name|indent
expr_stmt|;
block|}
specifier|public
name|void
name|println
parameter_list|(
name|Object
name|value
parameter_list|)
block|{
name|out
operator|.
name|print
argument_list|(
name|value
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|println
parameter_list|(
name|String
name|text
parameter_list|)
block|{
name|out
operator|.
name|print
argument_list|(
name|text
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|print
parameter_list|(
name|String
name|text
parameter_list|)
block|{
name|out
operator|.
name|print
argument_list|(
name|text
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|printIndent
parameter_list|()
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|indentLevel
condition|;
name|i
operator|++
control|)
block|{
name|out
operator|.
name|print
argument_list|(
name|indent
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|println
parameter_list|()
block|{
name|out
operator|.
name|println
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|incrementIndent
parameter_list|()
block|{
operator|++
name|indentLevel
expr_stmt|;
block|}
specifier|public
name|void
name|decrementIndent
parameter_list|()
block|{
operator|--
name|indentLevel
expr_stmt|;
block|}
specifier|public
name|int
name|getIndentLevel
parameter_list|()
block|{
return|return
name|indentLevel
return|;
block|}
specifier|public
name|void
name|setIndentLevel
parameter_list|(
name|int
name|indentLevel
parameter_list|)
block|{
name|this
operator|.
name|indentLevel
operator|=
name|indentLevel
expr_stmt|;
block|}
specifier|public
name|void
name|flush
parameter_list|()
block|{
name|out
operator|.
name|flush
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

