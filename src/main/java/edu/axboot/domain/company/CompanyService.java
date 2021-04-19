package edu.axboot.domain.company;

import com.chequer.axboot.core.parameter.RequestParams;
import com.querydsl.core.BooleanBuilder;
import edu.axboot.domain.BaseService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.List;

@Service
public class CompanyService extends BaseService<Company, Long> {

    private CompanyRepository companyRepository;

    @Inject
    public CompanyService(CompanyRepository companyRepository) {
        super(companyRepository);
        this.companyRepository = companyRepository;
    }

    public List<Company> gets(RequestParams<Company> requestParams) {
        return findAll();
    }

    //QueryDSL
    public List<Company> getByQueryDsl(RequestParams<Company> requestParams){
        String company = requestParams.getString("company", "");
        String ceo = requestParams.getString("ceo","");
        String bizno = requestParams.getString("bizno","");

        BooleanBuilder builder = new BooleanBuilder();

        if (isNotEmpty(company)) {
            builder.and(qCompany.companyNm.eq(company));
        }
        if (isNotEmpty(ceo)) {
            builder.and(qCompany.ceo.eq(ceo));
        }
        if (isNotEmpty(bizno)){
            builder.and(qCompany.bizno.eq(bizno));
        }

        List<Company> companyList = select()
                .from(qCompany)
                .where(builder)
                .orderBy(qCompany.companyNm.asc())
                .fetch();

        return companyList;
    }

    @Transactional
    public void saveByQueryDsl(List<Company> request) {
        for (Company company:request) {
            if (company.isCreated()) {
                save(company);
            } else if (company.isModified()) {
                update(qCompany)
                        .set(qCompany.companyNm, company.getCompanyNm())
                        .set(qCompany.ceo, company.getCeo())
                        //.set(qCompany.bizno, company.getBizno())
                        .where(qCompany.id.eq(company.getId()))
                        .execute();
            } else if (company.isDeleted()) {
                delete(qCompany)
                        .where(qCompany.id.eq(company.getId()))
                        .execute();
            }
        }
    }
}