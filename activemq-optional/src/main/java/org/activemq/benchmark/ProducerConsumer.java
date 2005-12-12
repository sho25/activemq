begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  *  * Copyright 2004 Protique Ltd  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  *  **/
end_comment

begin_package
package|package
name|org
operator|.
name|activemq
operator|.
name|benchmark
package|;
end_package

begin_comment
comment|/**  * @author James Strachan  * @version $Revision$  */
end_comment

begin_class
specifier|public
class|class
name|ProducerConsumer
extends|extends
name|Producer
block|{
specifier|private
name|Consumer
name|consumer
init|=
operator|new
name|Consumer
argument_list|()
decl_stmt|;
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
block|{
name|ProducerConsumer
name|tool
init|=
operator|new
name|ProducerConsumer
argument_list|()
decl_stmt|;
if|if
condition|(
name|args
operator|.
name|length
operator|>
literal|0
condition|)
block|{
name|tool
operator|.
name|setUrl
argument_list|(
name|args
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|args
operator|.
name|length
operator|>
literal|1
condition|)
block|{
name|tool
operator|.
name|setTopic
argument_list|(
name|parseBoolean
argument_list|(
name|args
index|[
literal|1
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|args
operator|.
name|length
operator|>
literal|2
condition|)
block|{
name|tool
operator|.
name|setSubject
argument_list|(
name|args
index|[
literal|2
index|]
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|args
operator|.
name|length
operator|>
literal|3
condition|)
block|{
name|tool
operator|.
name|setDurable
argument_list|(
name|Boolean
operator|.
name|getBoolean
argument_list|(
name|args
index|[
literal|3
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|args
operator|.
name|length
operator|>
literal|4
condition|)
block|{
name|tool
operator|.
name|setConnectionCount
argument_list|(
name|Integer
operator|.
name|parseInt
argument_list|(
name|args
index|[
literal|4
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|tool
operator|.
name|run
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Caught: "
operator|+
name|e
argument_list|)
expr_stmt|;
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
specifier|public
name|ProducerConsumer
parameter_list|()
block|{     }
specifier|public
name|void
name|run
parameter_list|()
throws|throws
name|Exception
block|{
name|consumer
operator|.
name|start
argument_list|()
expr_stmt|;
name|consumer
operator|.
name|subscribe
argument_list|()
expr_stmt|;
name|start
argument_list|()
expr_stmt|;
name|publish
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|setTopic
parameter_list|(
name|boolean
name|topic
parameter_list|)
block|{
name|super
operator|.
name|setTopic
argument_list|(
name|topic
argument_list|)
expr_stmt|;
name|consumer
operator|.
name|setTopic
argument_list|(
name|topic
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|setSubject
parameter_list|(
name|String
name|subject
parameter_list|)
block|{
name|super
operator|.
name|setSubject
argument_list|(
name|subject
argument_list|)
expr_stmt|;
name|consumer
operator|.
name|setSubject
argument_list|(
name|subject
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|setUrl
parameter_list|(
name|String
name|url
parameter_list|)
block|{
name|super
operator|.
name|setUrl
argument_list|(
name|url
argument_list|)
expr_stmt|;
name|consumer
operator|.
name|setUrl
argument_list|(
name|url
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|boolean
name|useTimerLoop
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
block|}
end_class

end_unit

