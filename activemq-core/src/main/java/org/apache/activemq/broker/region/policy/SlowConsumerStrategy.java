begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|broker
operator|.
name|region
operator|.
name|policy
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
name|broker
operator|.
name|ConnectionContext
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
name|broker
operator|.
name|region
operator|.
name|Subscription
import|;
end_import

begin_comment
comment|/*  * a strategy for dealing with slow consumers  */
end_comment

begin_interface
specifier|public
interface|interface
name|SlowConsumerStrategy
block|{
name|void
name|slowConsumer
parameter_list|(
name|ConnectionContext
name|context
parameter_list|,
name|Subscription
name|subs
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

