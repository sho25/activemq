begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|security
package|;
end_package

begin_import
import|import
name|org
operator|.
name|springframework
operator|.
name|beans
operator|.
name|factory
operator|.
name|DisposableBean
import|;
end_import

begin_import
import|import
name|org
operator|.
name|springframework
operator|.
name|beans
operator|.
name|factory
operator|.
name|InitializingBean
import|;
end_import

begin_comment
comment|/**  * A {@link DefaultAuthorizationMap} implementation which uses LDAP to initialize and update authorization  * policy.  *  * @org.apache.xbean.XBean  *  * @author<a href="http://hiramchirino.com">Hiram Chirino</a>  */
end_comment

begin_class
specifier|public
class|class
name|CachedLDAPAuthorizationMap
extends|extends
name|SimpleCachedLDAPAuthorizationMap
implements|implements
name|InitializingBean
implements|,
name|DisposableBean
block|{
annotation|@
name|Override
specifier|public
name|void
name|afterPropertiesSet
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|afterPropertiesSet
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|destroy
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|destroy
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

